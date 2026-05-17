# Release-scope audit: search, signals, aggregations

Tracking issue: `docs/tasks/issues/audit-kmp-search-readiness-and-signals-aggregations-release-scope.md`
(now closed; this file is the audit artifact). Related: Paperclip POY-262 /
POY-253 / POY-254.

This is a static-inspection audit only. Product, backend, and release
stakeholders own the final in/out-of-scope call for the next mobile
release — record their decision back here (or in Paperclip) once made.

## Endpoint readiness — as of 2026-05-17

| Backend endpoint | Generated KMP API | KMP consumer | Status |
|---|---|---|---|
| `GET /v1/search` (lexical) | `SearchApi.searchSummariesV1SearchGet` | `feature/summary/.../SearchRepositoryImpl.kt:28` | **Live.** Repository falls back to local DB search on network failure. |
| `GET /v1/search/semantic` | `SearchApi.semanticSearchSummariesV1SearchSemanticGet` | _none_ | **Generated, unwired.** No `feature/*` call site. |
| `GET /v1/signals` (+ siblings) | `SignalsApi.*` | _none_ | **Generated, unwired.** No `feature/*` call site. |
| `GET /v1/aggregations` (+ siblings) | `AggregationsApi.*` | _none_ | **Generated, unwired.** No `feature/*` call site. |

`SearchViewModel` and `SearchState` are wired to `SearchRepositoryImpl`
only, so the lexical-search UI flow is end-to-end. There are no UI
entry points that claim semantic, signals, or aggregations support
today — i.e. dropping them does not require any UI removal work.

## Default release-scope recommendation

Until product / backend confirm otherwise, the recommended posture
for the next mobile release is:

- **In scope:** `GET /v1/search` (lexical). Already shipping.
- **Out of scope:** `GET /v1/search/semantic`, `GET /v1/signals`,
  `GET /v1/aggregations`. Generated client surface exists so the
  consumer follow-ups can move quickly when prioritised, but none of
  the three is required for the lexical-search release goal.

Adopting any of the three later is a feature task, not an API task:
the contract is locked in via the generated client and is exercised
by the existing drift gate.

## If the decision flips to "include"

Each of the three deserves its own implementation issue with:

- Owner (KMP + UX).
- Repository contract (`SemanticSearchRepository` /
  `SignalsRepository` / `AggregationsRepository` in `feature/summary`
  per the existing layout).
- Mapper from generated DTO → domain model in
  `feature/summary/.../data/mappers/`.
- ViewModel + Compose entry point.
- Acceptance criteria: at minimum a focused unit test on the
  repository mapping (model after `SyncApplyResponseShapeTest`).

## How to verify this audit

```bash
# Lexical search consumer:
rg -n "SearchApi\." feature/

# Semantic / signals / aggregations consumers (expect nothing):
rg -n "semanticSearchSummariesV1SearchSemanticGet" feature/
rg -n "SignalsApi\." feature/
rg -n "AggregationsApi\." feature/
```
