# OpenAPI Client Migration — Status & Playbook

Tracks the feature-by-feature consumer migration from the hand-written
`feature/*/data/remote/*Api.kt` interfaces (which use `ApiResponseDto<T>`)
to the generated `core/api-generated/.../api/*Api` objects (which return
`Either<CallException, HttpCallResponse<T>>`).

## Status

Foundation is committed and pushed:

- `core/api-generated` module compiles. Generated tree at
  `src/commonMain/generated/` (4 files, ~9k lines) is checked in.
- Spec pinned in `tools/openapi.lock`; bumped via
  `./gradlew :core:api-generated:regenerateOpenApi`.
- CI runs `:core:api-generated:checkOpenApiDrift`.
- `bootstrapGeneratedApi(...)` initializes the singleton `Api`.
- `Either<CallException, HttpCallResponse<T>>.unwrap()` extension shims
  generated calls into the existing throw-on-error contract.

Consumer migration has **not** started. Every `feature/*` module still
depends on its hand-written `*Api.kt` and on `ApiResponseDto<T>` in
`core/data`.

## Why migration is paused — three spec gaps

The generator's value depends on the OpenAPI spec being complete and
typed. The current `mobile_api.yaml` has three classes of gaps that make
mechanical migration regress type safety in consumer code:

### 1. Endpoints missing from spec

`feature/auth/.../AuthApi.kt` exposes `loginWithApple(...)` and
`loginWithGoogle(...)`. The corresponding `/v1/auth/apple-login` and
`/v1/auth/google-login` paths do **not** exist in the spec's `paths:`
section. Generated `AuthenticationApi` only covers telegram, refresh,
logout, sessions, me, secret-login, credentials, secret-keys, telegram
linking, github, and session revocation.

**Open question:** Is Apple/Google login dead code, undocumented backend,
or aspirational? Resolve before migrating `feature/auth`.

### 2. Generic `JsonElement` response types

`feature/summary/.../QuickSaveApi.kt` returns
`ApiResponseDto<QuickSaveResponseDto>` — typed against a feature-private
`QuickSaveResponseDto` with `requestId`, `status`, `summaryId`,
`tagsAttached`, etc.

The generated equivalent:

```kotlin
public suspend fun quickSaveV1QuickSavePost(
    body: V1QuickSaveRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
): Either<CallException, HttpCallResponse<JsonElement>>
```

Response is `JsonElement` — untyped. Migration would force consumers to
hand-parse JSON, losing the type safety the hand-written DTO provided.

**Resolution path:** backend adds a proper response schema for
`/v1/quick-save` in the spec; mobile regen picks it up automatically.

### 3. Behavioral semantics not in spec

`feature/sync/.../KtorSyncApi.kt` wraps sync calls with
`retryWithBackoff(RetryPolicy.*)` — a feature-side retry/backoff helper.
The spec has no concept of retry policy. Migrating to generated calls
either (a) drops the retry behavior, (b) reimplements it as an
`HttpRequestBuilder` decorator passed per call, or (c) installs Ktor's
`HttpRequestRetry` plugin on `Api.client` and matches the per-endpoint
policy globally — none of which are mechanical.

## Per-feature audit (counts of hand-written methods)

Counted via `grep -cE "suspend fun [a-z]"` on each `*Api.kt` interface:

| Feature | API surface | Method count |
|---|---|---|
| `collections` | `CollectionsApi` | 18 |
| `digest` | `DigestApi` | 11 |
| `auth` | `AuthApi` | 9 |
| `collections` | `TagsApi`, `RulesApi`, `RssApi`, `ImportExportApi` | 7, 7, 7, 5 |
| `settings` | `BackupApi`, `UserPreferencesApi`, `NotificationsApi` | 8, 7, 1 |
| `summary` | `SummariesApi`, `SearchApi`, `RequestsApi`, `HighlightsApi`, `AudioApi`, `QuickSaveApi` | 8, 6, 5, 4, 2, 1 |
| `sync` | `SyncApi` | 4 |
| `auth` | `UserApi` | 4 |
| `digest` | `CustomDigestApi` | 3 |

Total: 19 interfaces, ~118 methods. Each migration also touches the
matching `Ktor*Api.kt`, the consuming `*RepositoryImpl.kt`, DTOs in
`data/remote/dto/`, the mapper layer in `data/mappers/`, the DI module's
`@Single` bindings, and tests.

## Recommended migration order (when spec gaps resolved)

1. **`feature/sync` first.** Smallest API (4 methods), but most behaviorally
   complex (retry). Doing it first proves the retry replacement pattern
   and the proactive-refresh wiring on `Api.client`.
2. **`feature/settings/NotificationsApi`** (1 method) — trivial, sets the
   straightforward migration template.
3. **`feature/auth`** — *after* the Apple/Google login decision.
4. **`feature/digest`** and **`feature/summary`** in parallel.
5. **`feature/collections`** last — largest surface, depends on `sync`.

## Per-feature migration recipe

For each feature, with no spec gap:

1. Identify the generated `*Api` object that corresponds to the
   hand-written interface. Method names follow the
   `<operationIdCamel><PathSegments><Method>` convention.
2. Rewrite `*RepositoryImpl.kt`:
   - Import the generated `*Api` object.
   - Replace each hand-written call with the generated call.
   - Use `.unwrap()` from `core/api-generated/.../bootstrap/EitherUnwrap.kt`
     to preserve the throw-on-error contract, OR adopt `Either.fold`
     directly if the repository is ready to expose `Either` to callers.
   - Update mappers — generated DTO field names match the spec
     (`@SerialName` preserved), camelCase'd in Kotlin. Re-derive the
     mapping from the new DTO shape.
3. Delete the hand-written `*Api.kt` and `Ktor*Api.kt`.
4. Delete feature-private request/response DTOs that are now generated.
   Keep feature-private domain models and any DTOs whose shape diverges
   from the spec (likely a sign of a spec gap — file an issue upstream).
5. Update `feature/<name>/.../di/<Name>FeatureModule.kt` — the deleted
   `@Single`-annotated Ktor impl no longer needs Koin registration.
6. Update tests. Tests mocking `ApiResponseDto<T>` need to mock the
   generated `Either<CallException, HttpCallResponse<T>>` instead.
   Consider Koin DSL overrides in test fixtures since the generated
   `*Api` are `object` singletons (not constructor-injectable).
7. Compile, run tests, verify the feature still works end-to-end.

## Auth refresh — open architectural decision

`bootstrapGeneratedApi(...)` registers a token provider but does not
install refresh-on-401. Three strategies are viable; pick before the
first migration that calls a secured endpoint:

| Strategy | Pros | Cons |
|---|---|---|
| **Proactive** — provider checks JWT expiry, refreshes if needed | No interceptor magic; matches our existing JWT structure | Has to parse the JWT (the companion's `JWT` helper) |
| **Reactive** — install Ktor `HttpRequestRetry` + a response interceptor that refreshes on 401 and retries once | Closest to existing `ApiClient` UX | Requires careful interaction with companion's `AuthPlugin` |
| **Explicit** — consumers handle 401 themselves via the `Either` left arm | Simplest plumbing; no magic | Every migrated repository needs to add explicit refresh logic; UX regression |

## Out of scope of any single feature migration

- Deleting `core/data/.../data/remote/dto/ApiResponseDto.kt` (and
  `ErrorResponseDto`, `MetaDto`, `PaginationDto`) requires every feature
  to be migrated first.
- Deleting `core/data/.../data/remote/ApiClient.kt` requires every
  feature to be off it AND auth refresh to be moved onto `Api.client`.
- `feature/sync`'s `retryWithBackoff` helper is in the feature module —
  decision on whether to replace with Ktor `HttpRequestRetry` plugin on
  `Api.client` belongs in the sync feature's migration PR.

## Re-running the regen

If the backend spec changes, bump the pin and regen:

```bash
# 1. Edit tools/openapi.lock — set "ref" to the new backend SHA.
# 2. Regen:
./gradlew :core:api-generated:regenerateOpenApi
# 3. Inspect the diff to src/commonMain/generated/ — this is the
#    contract change the migrating feature(s) need to absorb.
# 4. Commit YAML + generated tree together.
```
