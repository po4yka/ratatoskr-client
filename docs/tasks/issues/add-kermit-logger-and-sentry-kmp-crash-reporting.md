---
title: Add Kermit logger and Sentry KMP crash reporting
status: backlog
area: observability
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Kermit logger and Sentry KMP crash reporting #repo/ratatoskr-client #area/observability #status/backlog ⏫

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Observability).

## Objective

The repo has zero observability stack today — no Kermit, Sentry, Bugsnag, Crashlytics, or Firebase. `kotlin-logging` + `logback-android` only produce Logcat output that never reaches the team. Without crash reporting, real-world bugs go undetected until users churn.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Replace `kotlin-logging` shim usage with `co.touchlab:kermit` `Logger`.
- Add Sentry KMP SDK; init in the platform host (Android `RatatoskrApp`, iOS `iOSApp.swift`).
- Custom `Kermit -> Sentry` log writer forwards `Warn`/`Error` levels as Sentry breadcrumbs/events.
- DSN sourced from `local.properties`; release builds require it (build fails if missing).
- PII scrubbing wired through the existing `redactSensitiveBodyForLog` sanitizer.

## Constraints

- No PII in crash payloads.
- Sample rate ≤ 0.1 in release; full capture in debug.
- Opt-out toggle under Settings → Privacy.

## Definition of done

- A handled exception in any feature reaches Sentry dashboard.
- An unhandled crash on Android + iOS produces a Sentry event with a useful stack trace.
- PII-scrub assertion (synthetic event containing a fake token) lands at Sentry with the token redacted.
