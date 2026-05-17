---
title: Sweep stale doing task issues
status: backlog
area: docs
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Sweep stale doing task issues #repo/ratatoskr-client #area/docs #status/backlog 🔼

Filed from the 2026-05-17 deep audit (docs drift #8).

## Objective

All 8 pre-existing `docs/tasks/issues/*.md` files were marked `status: doing` simultaneously when the audit ran. Per `docs/tasks/README.md:55` and CLAUDE.md, completed work should have its issue file deleted, and "doing" is reserved for actively in-flight work. The canonical source of truth is uninformative until each issue's true status is reconciled.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Per-issue triage of: `add-decompose-component-and-route-tests-for-auth-summarylist-collections-digest-settings.md`, `add-ktor-bearer-refresh-and-token-rotation-tests.md`, `add-securestorage-round-trip-and-aead-key-persistence-tests.md`, `add-syncrepositoryimpl-integration-tests-and-offline-pending-op-drain-coverage.md`, `align-kmp-full-sync-request-with-backend-query-contract.md`, `audit-kmp-search-readiness-and-signals-aggregations-release-scope.md`, `fix-kmp-sync-apply-response-dtos-to-match-backend-contract.md`, `wire-ios-xctest-into-ios-yml-and-promote-detekt-to-merge-gate.md`.
- For each: confirm whether the work is actually in flight, complete (delete file), backlog (downgrade status), or blocked (move to blocked + reason).

## Constraints

- Status frontmatter and the `#status/*` tag in the canonical `- [ ]` line must match — update both.
- Update the `updated:` field on any touched file.

## Definition of done

- No more than 2 issue files marked `doing` simultaneously (reflects realistic single-developer parallelism).
- Done tasks deleted; git history (`git log -- docs/tasks/issues/<slug>.md`) preserves audit trail.
