---
title: Finish feature package refactor and drop legacy layout
status: backlog
area: kmp
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by:
  - move-feature-di-packages-to-feature-scoped-namespaces
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Finish feature package refactor and drop legacy layout #repo/ratatoskr-client #area/kmp #status/backlog 🔼

Filed from the 2026-05-17 deep audit (architecture M3).

## Objective

Every feature module has a half-finished package refactor: a legacy `com.po4yka.ratatoskr.{data,domain,presentation}` tree **and** a new `com.po4yka.ratatoskr.feature.<name>.{api,data,domain,ui}` tree, with classes scattered across both. Example: `feature/summary/.../data/repository/SummaryRepositoryImpl.kt` (legacy) alongside `feature/summary/.../feature/summary/data/sync/SummarySyncItemAppliers.kt` (new). Bindings reference both (`SummaryFeatureBindings.kt:3-12`). New contributors will pick the wrong location.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Move every class in each feature module under the legacy `com.po4yka.ratatoskr.{data,domain,presentation}` tree into `com.po4yka.ratatoskr.feature.<name>.{api,data,domain,ui}`.
- Delete the now-empty legacy package directories.
- Update all imports and DI scanner annotations.

## Constraints

- Must land after the DI-namespace task to avoid double-shuffling.
- Per-feature, atomic PRs to keep diffs reviewable.

## Definition of done

- `rg -l "^package com\\.po4yka\\.ratatoskr\\.(data|domain|presentation)" feature/` returns zero matches.
- All features compile and pass tests.
