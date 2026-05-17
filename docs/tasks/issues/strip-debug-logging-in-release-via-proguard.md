---
title: Strip debug logging in release via ProGuard rules
status: backlog
area: ops
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Strip debug logging in release via ProGuard rules #repo/ratatoskr-client #area/ops #status/backlog 🔽

Filed from the 2026-05-17 deep audit (security L8).

## Objective

`androidApp/proguard-rules.pro` does not strip `logger.debug` / `logger.trace` calls in release. R8 leaves the call sites in place; lambdas passed to kotlin-logging still allocate strings (even though most do lazy evaluation). Adding `-assumenosideeffects` rules for kotlin-logging's `debug`/`trace` removes them entirely from the release APK.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Add to `androidApp/proguard-rules.pro`:
  ```
  -assumenosideeffects class io.github.oshai.kotlinlogging.KLogger {
      public void debug(...);
      public void trace(...);
  }
  ```
- Audit other no-op log calls that can be stripped (`println`-style debug helpers).

## Constraints

- `error`, `warn`, `info` calls remain.
- Verify R8 actually performs the removal (check mapping output).

## Definition of done

- Release APK no longer contains debug log strings (verify via `apkanalyzer dex packages`).
- Crashlytics / logcat output in release still contains intended `info`/`warn`/`error` entries.
