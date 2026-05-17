---
title: Add pre-commit hook installer via lefthook
status: backlog
area: ops
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add pre-commit hook installer via lefthook #repo/ratatoskr-client #area/ops #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have engineering brainstorm (DX).

## Objective

Catch ktlint and design-system violations before they hit CI. No pre-commit infrastructure exists today (no `lefthook`, `husky`, `pre-commit`). Lefthook is language-agnostic and zero-runtime-dep — fits a KMP repo well.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `lefthook.yml` at repo root with hooks:
  - pre-commit → `./gradlew ktlintFormat --daemon` on staged Kotlin files
  - pre-commit → `scripts/check-no-m3.sh` (or the new Gradle task once that lands)
  - commit-msg → conventional-commit format check
- One-line install in `CONTRIBUTING.md`: `brew install lefthook && lefthook install`.

## Constraints

- Hooks must complete in under 5s on a routine commit.
- Skippable with `git commit --no-verify` for emergencies (but discouraged).

## Definition of done

- Fresh clone + `lefthook install` configures hooks correctly.
- Committing un-ktlint-formatted code auto-formats it (or fails with clear message).
- Documented in CONTRIBUTING.md and README.
