---
title: Wire Kermit + Sentry KMP and PII scrubbing into platform hosts
status: backlog
area: observability
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire Kermit + Sentry KMP and PII scrubbing into platform hosts #repo/ratatoskr-client #area/observability #status/backlog ⏫

Follow-up to `add-kermit-logger-and-sentry-kmp-crash-reporting` (landed the
durable testable atom: `core/common/.../util/observability/SentryEventScrubber.kt`
with a pure `scrub(text)` PII redactor over URL userinfo / Bearer tokens /
query-string credentials / emails — order-sensitive so the URL credential
fragment doesn't survive as an email match, with false-positive defenses
for `@channel` mentions, `asset@2x` qualifiers, and short "Bearer of bad
news" strings. 13 commonTest cases pin the contract.).

## Objective

Bring up the logger + crash reporter that consume the scrubber:

1. **Replace `kotlin-logging`** shim usage repo-wide with
   `co.touchlab:kermit` `Logger`. Configure logger with platform-specific
   `Severity` defaults (Verbose in debug, Warn in release).
2. **Add Sentry KMP SDK** and initialize in `RatatoskrApp.onCreate`
   (Android) and `iOSApp.swift` `init()` (iOS):
   - DSN sourced from `local.properties` `sentry.dsn` (release builds fail
     if unset).
   - Sample rate `0.1` in release, `1.0` in debug.
   - Add the privacy opt-out: a `UserPreferences.crashReportingEnabled`
     toggle defaulting to `true`, surfaced in Settings → Privacy.
3. **Custom `Kermit -> Sentry` log writer** that forwards `Warn` /
   `Error` levels as Sentry breadcrumbs / events. Pipe every breadcrumb
   message and every exception message through
   `SentryEventScrubber.scrub(text)` before send.
4. **Settings → Privacy** entry with a Frost `BracketSwitch` bound to
   `UserPreferences.crashReportingEnabled`.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Repo-wide migration from `kotlin-logging` to `kermit`.
- New Sentry KMP SDK dependency in `gradle/libs.versions.toml`.
- `core/common/.../observability/CrashReporter.kt` — `expect class`
  with Android (Sentry-Android), iOS (Sentry-Cocoa via KMP), and
  Desktop (no-op) actuals.
- `KermitSentryWriter.kt` — custom `LogWriter` that calls
  `SentryEventScrubber.scrub` before forwarding.
- `UserPreferences.crashReportingEnabled` + Settings UI.

## Constraints

- No PII in crash payloads — every outgoing text goes through
  `SentryEventScrubber.scrub`.
- Sample rate ≤ 0.1 in release; full capture in debug.
- Opt-out toggle under Settings → Privacy.

## Definition of done

- A handled exception in any feature reaches the Sentry dashboard.
- An unhandled crash on Android + iOS produces a Sentry event with a
  useful stack trace.
- Synthetic event containing a fake token + email lands at Sentry with
  both redacted.
- Opt-out toggle stops new events from being sent.
