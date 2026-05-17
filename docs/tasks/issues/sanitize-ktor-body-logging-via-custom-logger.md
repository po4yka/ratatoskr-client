---
title: Sanitize Ktor body logging via custom Logger
status: backlog
area: observability
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by:
  - enforce-api-logging-disabled-in-ios-and-desktop-release-builds
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Sanitize Ktor body logging via custom Logger #repo/ratatoskr-client #area/observability #status/backlog ⏫

Filed from the 2026-05-17 deep audit (C1 code quality).

## Objective

Even with the release-mode clamp in place, debug logging at `LogLevel.ALL` leaks every non-auth request/response body verbatim through `Logger.DEFAULT`. The path-based `filter` block only suppresses bodies for `/auth/*` endpoints, leaving any future endpoint that returns PII or embedded tokens unprotected. The `redactSensitiveBodyForLog` helper at `ApiClient.kt:60-105` is correct but never wired into the `Logging` plugin's body logging.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Custom Ktor `Logger` (or `format` block) that funnels every log line through `redactSensitiveBodyForLog` before delegating to the platform logger.
- Default level lowered to `LogLevel.HEADERS` outside debug builds.
- Unit test asserting that a body containing `"refresh_token"` / `"access_token"` / `"Authorization"` is redacted before reaching the test logger.

## Constraints

- Sanitizer must remain pure — no I/O, no allocations beyond the redacted string.
- Authorization-header redaction at `sanitizeHeader` (`ApiClient.kt:147-149`) preserved.

## Definition of done

- `LogLevel.ALL` in debug never emits an unredacted token-shaped payload (verified by test).
- Codebase scan for `"refresh_token"` in log output confirms zero leaks.
