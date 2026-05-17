---
title: Promote check-no-m3 script to Gradle check task
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Promote check-no-m3 script to Gradle check task #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Design-System Tooling).

## Objective

`scripts/check-no-m3.sh` enforces the "no Material 3 in commonMain" Frost rule, but it's a shell script that contributors must remember to run. CI doesn't wire it to `./gradlew check`. Promote it to a real Gradle task so violations fail the build.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `verifyNoMaterial3` Gradle task in `build-logic/src/main/kotlin/com/po4yka/ratatoskr/ratatoskr.architecture-checks.gradle.kts`.
- Task scans `commonMain` source sets for `androidx.compose.material3.*` imports.
- `check` depends on it; CI's `status-check` gate includes it (already covers `:check`).
- Allowlist exception file for genuine cases (none today).

## Constraints

- Must work via the configuration-cache.
- Error message points to DESIGN.md and lists the offending files.

## Definition of done

- Adding a Material 3 import in commonMain fails `./gradlew check`.
- CI fails on a synthetic violation PR.
- `scripts/check-no-m3.sh` retired (or aliased to the Gradle task for muscle-memory).
