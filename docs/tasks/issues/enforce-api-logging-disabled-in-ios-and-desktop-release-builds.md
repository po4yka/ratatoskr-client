---
title: Enforce api.logging.enabled=false in iOS and desktop release builds
status: backlog
area: observability
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Enforce api.logging.enabled=false in iOS and desktop release builds #repo/ratatoskr-client #area/observability #status/backlog 🔺

Filed from the 2026-05-17 deep audit (K1 / security M2).

## Objective

`core/data/.../data/remote/ApiClient.kt:140-150` installs Ktor `Logging` with `level = LogLevel.ALL` whenever `AppConfig.Api.loggingEnabled == true`. `androidApp/build.gradle.kts:64-67,84` correctly hardcodes `API_LOGGING_ENABLED=false` for the release build type, but there is no analogous compile-time clamp on iOS or desktop — `AppConfig.Api.loggingEnabled` is read from `local.properties` / `Config.xcconfig`. A misconfigured TestFlight or App Store iOS build can ship with `LogLevel.ALL` enabled. The `redactSensitiveBodyForLog` helper is excellent but only runs in the `HttpCallValidator` error handler, not the `Logging` plugin path.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- iOS: build-config flag fed into `AppConfig.Api.loggingEnabled` that forces `false` for release configurations. Audit `iosApp/Configuration/Config.xcconfig` setup.
- Desktop: same compile-time clamp in `composeApp` desktop main.
- Unit test (or assertion at bootstrap) that fails when a release build observes `loggingEnabled == true`.

## Constraints

- Debug builds must retain current behavior.
- No regression to log sanitization helpers.

## Definition of done

- Release-mode iOS + desktop bootstraps `loggingEnabled = false` regardless of properties.
- Manually verified by toggling release build and inspecting that no Ktor request bodies are logged.
- Documented in CLAUDE.md / AGENTS.md "Configuration" section.
