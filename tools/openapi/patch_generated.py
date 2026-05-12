"""Post-generation patches for openapi-kmp-gen 1.3.0 output.

The generator emits code with two known bugs we have to paper over:

1. URL-parameter shadowing: services with a query parameter literally named
   `url` produce method signatures like `fun foo(url: String, ...)` whose body
   calls `url.appendPathSegments(...)`. The latter refers to the Ktor request
   builder's `url` property which is now shadowed. We rename the parameter
   to `reqUrl` in both the signature and its usage as a query value.

2. Non-deterministic Api.kt timestamp: the generator embeds
   `public const val createdAt: String = "<iso8601>"` in Api.kt, producing
   a spurious diff every regen. Strip the line.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path


def _patch_url_shadow(text: str) -> str:
    # Pattern: `public suspend fun NAME(url: String, ...): Either<...> = Api.client.eitherRequest {`
    # followed by a body that uses `url.appendPathSegments(...)` (Ktor builder)
    # AND `appendSerializedQueryParameter(name = "url", value = url, ...)` (the param).
    # Rename param `url` → `reqUrl` and the only usage that matches the param value.
    # Pattern variants of the offending parameter declaration:
    #   - `(url: String, ` (start of params, single-line sig)
    #   - `, url: String, ` / `, url: String?, ` (mid-list)
    #   - `<indent>url: String,?` (multi-line param block)
    # And the usage we must update in lockstep:
    #   - `name = "url", value = url`
    substitutions = [
        ("(url: String,", "(reqUrl: String,"),
        ("(url: String)", "(reqUrl: String)"),
        (", url: String,", ", reqUrl: String,"),
        (", url: String)", ", reqUrl: String)"),
        ('name = "url", value = url,', 'name = "url", value = reqUrl,'),
        ('name = "url", value = url)', 'name = "url", value = reqUrl)'),
    ]
    out: list[str] = []
    param_line_re = re.compile(r"^(\s+)url: (String[?]?)(,?)$")
    for line in text.split("\n"):
        m = param_line_re.match(line)
        if m:
            indent, typ, comma = m.groups()
            line = f"{indent}reqUrl: {typ}{comma}"
        else:
            for old, new in substitutions:
                line = line.replace(old, new)
        out.append(line)
    return "\n".join(out)


def _strip_timestamp(text: str) -> str:
    return re.sub(
        r'\n  public const val createdAt: String = "[^"]+"\n',
        "\n",
        text,
    )


def patch_file(path: Path) -> bool:
    """Return True if file changed."""
    original = path.read_text()
    patched = original
    if path.name == "Api.kt":
        patched = _strip_timestamp(patched)
    if path.name == "Services.kt":
        patched = _patch_url_shadow(patched)
    if patched != original:
        path.write_text(patched)
        return True
    return False


def main(argv: list[str]) -> int:
    if len(argv) < 2:
        print("usage: patch_generated.py <generated_root>", file=sys.stderr)
        return 2
    root = Path(argv[1])
    changed = 0
    for kt in root.rglob("*.kt"):
        if patch_file(kt):
            changed += 1
            print(f"patched {kt.relative_to(root)}", file=sys.stderr)
    print(f"{changed} files patched", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
