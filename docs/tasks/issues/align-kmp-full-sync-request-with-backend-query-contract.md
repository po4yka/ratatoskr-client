---
title: Align KMP full-sync request with backend query contract
status: doing
area: sync
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
paperclip: POY-259
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Align KMP full-sync request with backend query contract #repo/ratatoskr-client #area/sync #status/doing ⏫ [paperclip:POY-259]

## Objective

Remove or justify the unsupported `cursor` query parameter sent by ratatoskr-client full-sync calls.

## Context

Contract map POY-253 found KMP `fullSync` sends a `cursor` query parameter, while backend full sync currently accepts `session_id` and `limit` only. The default path is to remove the unsupported client parameter unless a backend contract change is approved.

## Owner

Senior KMP / Compose Multiplatform Engineer. Coordinate with the backend team if any backend contract change is proposed.

## Priority

High.

## Parent issue or goal linkage

Related: POY-253. Goal: Ratatoskr ecosystem mobile contract and release-readiness baseline. Project: ratatoskr-client.

## Acceptance criteria

- Confirm the current backend full-sync query contract from docs/openapi/mobile_api.yaml and router/model code.
- Remove the unsupported `cursor` parameter from the KMP full-sync request path, or document why a backend contract change is required and get explicit approval before proceeding.
- Add/update a focused test or API request-construction assertion if available.
- Preserve feature/sync ownership and offline-first sync behavior.

## Expected artifact

KMP client code change or explicit backend-team-approved contract-change note.

## Constraints

Do not edit backend OpenAPI directly. Do not run live API calls.

## Risks

Unsupported query parameters can hide client/server drift and complicate cache/ETag sync semantics.

## Verification plan

Run the smallest relevant sync API/repository test; report exact Gradle task.

## Definition of done

KMP full-sync request matches the backend contract or a deliberate backend contract-change issue exists with a named owner.
