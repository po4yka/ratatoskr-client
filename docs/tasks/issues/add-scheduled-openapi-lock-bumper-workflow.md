---
title: Add scheduled OpenAPI lock bumper workflow
status: backlog
area: ci
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add scheduled OpenAPI lock bumper workflow #repo/ratatoskr-client #area/ci #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Automation).

## Objective

`tools/openapi.lock` is bumped manually today, but the existing drift gate already catches incompatibilities. A weekly bot PR that runs `regenerateOpenApi` against the upstream HEAD reduces toil and surfaces backend contract changes proactively.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `.github/workflows/openapi-bump.yml`:
  - Cron weekly (Mondays 09:00 UTC).
  - Fetches latest backend `mobile_api.yaml` SHA.
  - Updates `tools/openapi.lock`.
  - Runs `./gradlew :core:api-generated:regenerateOpenApi`.
  - Opens a PR if the diff is non-empty, with the upstream commit-range diff link in the description.
- Auto-label `area:openapi` (depends on the labeler workflow if introduced separately).

## Constraints

- PR is non-mergeable until manual review (humans validate backend contract intent).
- Bot uses a least-privilege token, no force-push.

## Definition of done

- Bot opens a PR on a synthetic upstream change.
- PR contains the regenerated Kotlin + updated lock + drift-check passing.
- No-op week produces no PR.
