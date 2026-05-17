---
title: Move feature DI packages to feature-scoped namespaces
status: backlog
area: kmp
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Move feature DI packages to feature-scoped namespaces #repo/ratatoskr-client #area/kmp #status/backlog ⏫

Filed from the 2026-05-17 deep audit (architecture H2).

## Objective

Every feature's DI file declares `package com.po4yka.ratatoskr.di` — same Kotlin package across separate Gradle modules:

- `feature/auth/.../di/AuthFeatureBindings.kt:1`
- `feature/sync/.../di/SyncFeatureBindings.kt:1`
- `feature/summary/.../di/SummaryFeatureBindings.kt:1`
- collections, digest, settings — same.

And `core/common/.../di/CoreCommonModule.kt:6-7` declares `@ComponentScan("com.po4yka.ratatoskr.domain.usecase")` — also a shared package across modules. Works in JVM/Android, but is fragile for iOS framework export and confuses tooling (KSP, R8). Only file-name disambiguation keeps the generated KSP module classes from colliding (`ArchitectureBoundaryTest.kt:66-75`).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Move each feature's DI file to `com.po4yka.ratatoskr.feature.<name>.di`.
- Move use cases to `com.po4yka.ratatoskr.feature.<name>.domain.usecase` (already the pattern for new code under `feature/<name>/api,data,domain,ui` — finish the migration).
- Update `@ComponentScan` arguments accordingly.
- Update `composeApp/.../di/KoinInitializer.kt` references.

## Constraints

- KSP-generated module class names must remain unique across modules.
- No behavior change to DI graph.

## Definition of done

- `rg "^package com\\.po4yka\\.ratatoskr\\.di" feature/` returns zero matches.
- All `@ComponentScan` arguments anchor to feature-scoped subpackages.
- `./gradlew :composeApp:assembleDebug` green on Android + iOS.
