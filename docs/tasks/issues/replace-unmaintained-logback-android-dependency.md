---
title: Replace unmaintained logback-android dependency
status: backlog
area: ops
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Replace unmaintained logback-android dependency #repo/ratatoskr-client #area/ops #status/backlog ⏫

Filed from the 2026-05-17 deep audit (build H).

## Objective

`logback-android = "3.0.0"` (`gradle/libs.versions.toml:37`) is `com.github.tony19:logback-android` 3.0.0 from 2021 — the artifact is effectively unmaintained. It is wired into Android `core/data` (`core/data/build.gradle.kts:44`). Logback-classic 1.5.32 (desktop) is current and patched, but for Android we should move off the abandoned mirror.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Replace `logback-android` with one of:
  - `slf4j-android` (lightweight bridge, minimal features), OR
  - rely on `io.github.oshai:kotlin-logging` only and let Android use the default `android.util.Log` appender via `slf4j-simple`.
- Audit which features of logback we actually use (rolling files? appenders?) and replicate only those.

## Constraints

- No log-format change beyond what consumers (Android Studio Logcat, Bugsnag/Crashlytics if any) tolerate.
- PII redaction still routed through the existing sanitizer.

## Definition of done

- `logback-android` removed from the version catalog.
- Android release build smoke-tested — logs reach Logcat.
- No `org.slf4j` warnings in startup logs.
