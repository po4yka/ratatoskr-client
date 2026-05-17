---
title: Complete generated-client migration and delete legacy ApiClient
status: backlog
area: api
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by:
  - unify-http-client-refresh-via-shared-token-refresher
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Complete generated-client migration and delete legacy ApiClient #repo/ratatoskr-client #area/api #status/backlog 🔺

Filed from the 2026-05-17 deep audit (cross-cutting finding CC2).

## Objective

Honor the migration plan in CLAUDE.md "OpenAPI Generation" → "Bootstrapping the generated client": delete `core/data/.../data/remote/ApiClient.kt`, `ApiResponseDto<T>`, and the hand-written feature DTOs that overlap with `core/api-generated`. The audit confirms zero feature modules still import `ApiClient`; the only remaining `*Api*.kt` is `feature/summary/.../data/remote/KtorProxyApi.kt` (image proxy URL builder, not a backend client).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Removal of `ApiClient.kt`, `ApiResponseDto.kt`, `HttpErrorMapper` callers consolidated.
- Inventory + removal of hand-written DTOs in `feature/summary/.../data/remote/dto/` (21 classes) that overlap with `core/api-generated` models; keep only ones with no generated equivalent.
- Verification that `core/data/.../di/NetworkModule.kt` no longer registers any ApiClient bindings, and that no DI graph references survive.
- Updated CLAUDE.md / AGENTS.md / docs/ARCHITECTURE.md sections that describe the migration as complete (handled in the doc-sync task).

## Constraints

- Must land **after** the shared `TokenRefresher` task (this task removes the hand-written 401 refresh path).
- Frost-themed UI must continue to render unchanged.
- All existing `feature/*:allTests` must pass.

## Definition of done

- `rg "import com\.po4yka\.ratatoskr\.data\.remote\.ApiClient"` returns no matches.
- `rg "ApiResponseDto"` returns no matches outside deleted history.
- `./gradlew build` green on Android + iOS sim + desktop.
- Diff is dominated by deletions; no new abstractions introduced.
