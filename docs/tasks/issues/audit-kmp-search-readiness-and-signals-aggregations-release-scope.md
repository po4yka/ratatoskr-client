---
title: Audit KMP search readiness and signals/aggregations release scope
status: backlog
area: search
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
paperclip: POY-262
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Audit KMP search readiness and signals/aggregations release scope #repo/ratatoskr-client #area/search #status/backlog 🔼 [paperclip:POY-262]

## Objective

Audit ratatoskr-client readiness for search DTOs and decide whether `/v1/signals` and `/v1/aggregations` are in or out of the next mobile release.

## Context

Contract map POY-253 found search exists on both backend and KMP but needs endpoint-by-endpoint DTO verification. Backend also exposes `/v1/signals` and `/v1/aggregations`; no KMP API surface was found. This should be an explicit release-scope decision, not accidental omission.

## Owner

Senior KMP / Compose Multiplatform Engineer. Coordinate with product and backend teams for release-scope decisions.

## Priority

Medium.

## Parent issue or goal linkage

Related: POY-253 and POY-254. Goal: Ratatoskr ecosystem mobile contract and release-readiness baseline. Project: ratatoskr-client.

## Acceptance criteria

- Verify KMP search DTOs and repository behavior against backend `/v1/search` and `/v1/search/semantic` parameters and response envelope.
- Decide, with product and backend team input, whether signals and aggregations are excluded from the next mobile release or require KMP surfaces now.
- If excluded, document the release-scope decision in Paperclip and ensure no UI path implies availability.
- If included, create concrete implementation issues with owner, API surface, tests, and UX acceptance criteria.

## Expected artifact

Readiness audit comment plus any concrete follow-up issues.

## Constraints

Do not implement signals/aggregations in this issue unless explicitly split and assigned. Do not change backend API contract.

## Risks

Unclear release scope can produce broken navigation, missing API clients, or user-visible claims for unavailable features.

## Verification plan

Static DTO/repository inspection and, where existing, focused KMP tests for search request/response mapping.

## Definition of done

Search readiness is classified, and signals/aggregations are explicitly in-scope or out-of-scope for the next mobile release.
