---
title: Add Compose UI screenshot tests via Roborazzi
status: backlog
area: testing
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by:
  - add-frost-lab-component-browser-screen
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Compose UI screenshot tests via Roborazzi #repo/ratatoskr-client #area/testing #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Build/Test).

## Objective

Lock down the Frost visual contract with image diffs. Today zero screenshot tests — any accent-color drift, spacing regression, or theme bug ships silently. Roborazzi runs on Robolectric so tests are fast and CI-friendly.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Add `roborazzi` to `gradle/libs.versions.toml`.
- `core/ui/build.gradle.kts` Android `unitTest` source set with Robolectric.
- New `core/ui/src/androidUnitTest/.../FrostSnapshotTest.kt` iterating over the same atoms enumerated by the Frost Lab.
- Snapshot PNGs stored under `core/ui/src/test/snapshots/`.

## Constraints

- Snapshots are deterministic — no random data, no system time.
- CI uses identical Robolectric SDK version on every run.
- Diff threshold tunable per atom.

## Definition of done

- A Frost atom color change triggers a failing snapshot test.
- `./gradlew :core:ui:verifyRoborazzi` green on a clean checkout.
- Snapshots reviewed and committed (signed off by design).
