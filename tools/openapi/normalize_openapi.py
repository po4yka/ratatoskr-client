"""Normalize OAS 3.1 nullable unions to OAS 3.0 nullable:true.

Rewrites `anyOf|oneOf: [<X>, {type: 'null'}]` shapes to OAS 3.0-compatible
nullable scalars, refs, or anyOf-with-nullable-flag, so that older code
generators that don't understand `type: 'null'` (specifically
openapi-kmp-gen 1.3.0) produce sane Kotlin types.

Rules:
  1. anyOf/oneOf containing exactly two variants where one is {type: 'null'}
     and the other is a primitive schema (with optional format/min/max/etc.)
     → flatten to that primitive + nullable: true
  2. anyOf/oneOf where one variant is {type: 'null'} and the other is
     a $ref → wrap the $ref in allOf and add nullable: true
  3. anyOf/oneOf with 3+ variants including {type: 'null'}
     → drop the null variant, add nullable: true at the parent level
  4. Bare `type: 'null'` outside a union (rare) → emit `nullable: true` with
     no type, which most generators accept.

Usage:
  python3 normalize_openapi.py input.yaml output.yaml
  python3 normalize_openapi.py --in-place input.yaml
"""

from __future__ import annotations

import sys
from pathlib import Path
from typing import Any

import yaml  # type: ignore[import-untyped]


def _is_null_schema(node: Any) -> bool:
    if not isinstance(node, dict):
        return False
    if node.get("type") == "null" and len(node) == 1:
        return True
    if node.get("nullable") is True and len(node) == 1:
        return True
    return False


def _merge_into(target: dict, source: dict) -> None:
    """Copy keys from source into target without overwriting existing keys."""
    for k, v in source.items():
        if k == "type" and v == "null":
            continue
        target.setdefault(k, v)


def normalize(node: Any) -> Any:
    if isinstance(node, list):
        return [normalize(v) for v in node]
    if not isinstance(node, dict):
        return node

    # Recurse first, so children are normalized.
    for k, v in list(node.items()):
        node[k] = normalize(v)

    for union_key in ("anyOf", "oneOf"):
        variants = node.get(union_key)
        if not isinstance(variants, list) or not variants:
            continue
        null_count = sum(1 for v in variants if _is_null_schema(v))
        if null_count == 0:
            continue
        non_null = [v for v in variants if not _is_null_schema(v)]
        if not non_null:
            node.pop(union_key)
            node["nullable"] = True
            continue
        if len(non_null) == 1:
            only = non_null[0]
            node.pop(union_key)
            if isinstance(only, dict) and "$ref" in only:
                node["allOf"] = [only]
                node["nullable"] = True
            else:
                _merge_into(node, only if isinstance(only, dict) else {})
                node["nullable"] = True
            continue
        node[union_key] = non_null
        node["nullable"] = True

    if node.get("type") == "null":
        node.pop("type")
        node["nullable"] = True

    return node


POLYMORPHIC_FLATTEN: dict[str, str] = {
    "SyncApplyItem.id": "string",
    "ValidationError.loc.items": "string",
}


def flatten_polymorphic_unions(spec: dict) -> None:
    """Collapse anyOf/oneOf unions with multiple non-null variants to a single
    chosen primitive type. openapi-kmp-gen emits empty data classes for these.
    Loss of polymorphism is accepted in exchange for compilable output.
    """
    schemas = (spec.get("components") or {}).get("schemas") or {}

    sai = schemas.get("SyncApplyItem")
    if isinstance(sai, dict):
        props = sai.get("properties") or {}
        if "id" in props:
            props["id"] = {"type": "string", "title": "Id"}

    ve = schemas.get("ValidationError")
    if isinstance(ve, dict):
        props = ve.get("properties") or {}
        loc = props.get("loc")
        if isinstance(loc, dict) and isinstance(loc.get("items"), dict):
            loc["items"] = {"type": "string"}


def strip_nullable_from_required(node: Any) -> None:
    """Remove fields marked `nullable: true` from sibling `required` lists.

    Some generators (openapi-kmp-gen 1.3.0) treat `required` as forbidding
    null, contradicting the OpenAPI spec where required only requires the
    KEY to be present. Stripping nullable fields from required makes the
    generator emit `T? = null` instead of non-nullable `T`.
    """
    if isinstance(node, list):
        for item in node:
            strip_nullable_from_required(item)
        return
    if not isinstance(node, dict):
        return
    props = node.get("properties")
    required = node.get("required")
    if isinstance(props, dict) and isinstance(required, list):
        node["required"] = [
            r for r in required
            if not (isinstance(props.get(r), dict) and props[r].get("nullable") is True)
        ]
    for v in node.values():
        strip_nullable_from_required(v)


def main(argv: list[str]) -> int:
    in_place = "--in-place" in argv
    args = [a for a in argv[1:] if not a.startswith("--")]
    if in_place:
        if len(args) != 1:
            print("usage: normalize_openapi.py --in-place <path>", file=sys.stderr)
            return 2
        src = dst = Path(args[0])
    else:
        if len(args) != 2:
            print("usage: normalize_openapi.py <input> <output>", file=sys.stderr)
            return 2
        src, dst = Path(args[0]), Path(args[1])

    spec = yaml.safe_load(src.read_text())
    normalized = normalize(spec)
    strip_nullable_from_required(normalized)
    flatten_polymorphic_unions(normalized)
    dst.write_text(yaml.safe_dump(normalized, sort_keys=False, allow_unicode=True, width=1000))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
