---
title: Fix KMP sync-apply response DTOs to match backend contract
status: backlog
area: sync
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
paperclip: POY-258
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Fix KMP sync-apply response DTOs to match backend contract #repo/ratatoskr-client #area/sync #status/backlog ⏫ [paperclip:POY-258]

## Objective

Update ratatoskr-client sync-apply response handling so it matches the current Ratatoskr backend contract.

## Context

Contract map POY-253 found the release-blocking gap: backend `/v1/sync/apply` returns session-level `sessionId`, `results[]`, `conflicts[]`, and `hasMore` with camelCase aliases, while KMP expects an older `applied`, `server_version`, `new_server_version` style shape. Backend contract stands; KMP should adapt.

## Owner

Senior KMP / Compose Multiplatform Engineer. Coordinate with backend and release teams.

## Priority

High.

## Parent issue or goal linkage

Related: POY-253. Goal: Ratatoskr ecosystem mobile contract and release-readiness baseline. Project: ratatoskr-client. This is not a formal child issue because Paperclip rejected cross-project child linkage.

## Acceptance criteria

- Update sync apply response DTOs/mappers/repository handling to consume backend `SyncApplyResponseData` and item result/conflict shape.
- Preserve feature/sync ownership and do not leak transport DTOs into domain/UI layers.
- Add or update focused tests for success, conflict, and `hasMore` cases.
- Document any backend ambiguity back on POY-253 rather than changing backend shape.

## Expected artifact

KMP client code change plus focused test evidence.

## Constraints

Do not change backend contract or docs/openapi/mobile_api.yaml in this issue. Do not run live API calls. Follow ratatoskr-client architecture and Frost constraints.

## Risks

Incorrect mapping can silently drop sync conflicts or corrupt offline-first pending operation state.

## Verification plan

Run the smallest relevant feature/sync tests first; if unavailable, add targeted unit tests and report exact Gradle task.

## Definition of done

KMP sync apply deserializes and maps the backend response shape, tests cover success/conflict/hasMore, and the release gate in POY-255 can include it.
