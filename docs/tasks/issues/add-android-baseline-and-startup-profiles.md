---
title: Add Android baseline and startup profiles
status: backlog
area: kmp
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android baseline and startup profiles #repo/ratatoskr-client #area/kmp #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Performance).

## Objective

Cut cold-start and first-frame jank on Android by shipping AOT-compiled critical paths. Today nothing under `androidApp/` references `androidx.baselineprofile` or `androidx.profileinstaller`.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `androidApp:benchmark` Gradle module applying `androidx.baselineprofile`.
- Profile generator exercises: `MainActivity` → `RootComponent` → `SummaryListScreen` first frame + scroll + open detail.
- `gradle/libs.versions.toml` adds `androidx.benchmark`, `androidx.profileinstaller`.
- Profile bundled into release builds via the plugin.

## Constraints

- Profile generation runs on a real device or emulator — keep it opt-in in CI initially.
- Don't regress build times for routine debug builds.

## Definition of done

- Release build ships an embedded baseline profile.
- Cold-start measurement (Macrobenchmark) shows measurable improvement vs no-profile baseline.
- CI workflow generates the profile on a scheduled cadence (not per-PR).
