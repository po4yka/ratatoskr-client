---
title: Delete dead core/src module scaffolding
status: backlog
area: ops
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Delete dead core/src module scaffolding #repo/ratatoskr-client #area/ops #status/backlog 🔺

Filed from the 2026-05-17 deep audit (K3 / C1 architecture).

## Objective

`core/src/androidMain/AndroidManifest.xml` is the only file under `core/src/`. There is no `:core` module in `settings.gradle.kts` (only `:core:common`, `:core:data`, `:core:navigation`, `:core:ui`, `:core:api-generated`), no `build.gradle.kts`, no Kotlin sources. The stray manifest declares an `ACCESS_NETWORK_STATE` permission that nothing consumes. It misleads contributors, IDE indexing, and lint manifest scans into believing `core` is a Gradle module.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `rm -r core/src/` (only `androidMain/AndroidManifest.xml` lives there).
- Confirm no `.gitignore`, scripts, or CI reference `core/src/`.

## Constraints

- Must not touch the real modules under `core/*/src/`.

## Definition of done

- `./gradlew build` green.
- `find core/src` returns no match.
- Diff is pure deletion.
