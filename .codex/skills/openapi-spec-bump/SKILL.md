---
name: openapi-spec-bump
description:
  Use when bumping the pinned OpenAPI spec for the Ratatoskr mobile API,
  regenerating the Kotlin client in core/api-generated, debugging drift
  failures, or touching tools/openapi.lock, mobile_api.yaml, or files
  under core/api-generated/src/commonMain/generated/. Covers the
  normalizer, post-gen patch, drift check, and the rule against
  hand-editing derived artifacts.
user-invocable: false
---

# Bumping the OpenAPI Spec

The Kotlin mobile API client is generated from a pinned upstream YAML
via [openapi-kmp-gen](https://github.com/kroegerama/openapi-kmp-gen).
Three artifacts derive from the pin and **must not be hand-edited**:

- `core/api-generated/src/commonMain/openapi/mobile_api.yaml`
  (the fetched, normalized YAML — checked in so builds work offline)
- everything under `core/api-generated/src/commonMain/generated/`
  (4 files, ~9k lines of Kotlin)
- the normalized form that the drift check compares against

The pin itself lives at `tools/openapi.lock` (repo + commit SHA + path).

## Workflow

1. Edit `tools/openapi.lock` with the new backend commit SHA.
2. Regenerate everything in one step:
   ```bash
   ./gradlew :core:api-generated:regenerateOpenApi
   ```
   This fetches the upstream YAML, runs `tools/openapi/normalize_openapi.py`,
   invokes openapi-kmp-gen, and applies `tools/openapi/patch_generated.py`.
3. `git diff` to review the contract changes.
4. Commit the lock bump + regenerated artifacts as a single change.

## Drift detection

`./gradlew check` runs `:core:api-generated:checkOpenApiDrift`, which
fetches the pinned upstream YAML, normalizes it, and byte-compares
against the checked-in copy. Any local edit to the YAML or the
generated sources will fail this check — the build will reject your
PR before merge.

## Why the normalizer + patches exist

openapi-kmp-gen 1.3.0 has three known bugs against our spec; the
normalizer and post-gen patch exist solely to work around them:

1. **OAS 3.1 `anyOf` unions with `{type: "null"}`** produce empty data
   classes. `tools/openapi/normalize_openapi.py` rewrites them to OAS
   3.0 `nullable: true` form before generation.
2. **Polymorphic `anyOf` without null** (e.g. `int | string`) also
   produce empty classes. The normalizer flattens the two known sites
   to `string`.
3. **Generated `url` parameter shadowing** — function parameters named
   `url` shadow the Ktor request builder's `url` property; the
   generator also emits a non-deterministic timestamp into `Api.kt`.
   `tools/openapi/patch_generated.py` renames the parameter to
   `reqUrl` and strips the timestamp.

If a regen introduces a **new** failure shape, extend the normalizer
or post-gen patch — never hand-edit the generated output.

## Generated client at runtime

`Api` is a singleton (`object Api : ApiHolder()`) with its own
`HttpClient`. Bootstrap once during app startup via
`bootstrapGeneratedApi(...)` in
`core/api-generated/.../bootstrap/GeneratedApiBootstrap.kt`:

```kotlin
bootstrapGeneratedApi(
    baseUrl = AppConfig.Api.baseUrl,
    engine = platformHttpEngine,
    bearerTokenProvider = { secureStorage.getAccessToken() },
    withLogging = AppConfig.Api.loggingEnabled,
)
```

The companion library's `AuthPlugin` does **not** auto-refresh on 401.
Hand-written call sites still go through `core/data/.../ApiClient.kt`
for bearer refresh; new code calls the generated `<Name>Api` objects
directly and handles `Either<CallException, HttpCallResponse<T>>` at
the repository layer.

When the last hand-written API call site is removed, the existing
`ApiClient` and `ApiResponseDto<T>` envelope can be deleted in a
single follow-up commit.
