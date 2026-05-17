---
title: Add Ktor bearer refresh and token rotation tests
status: backlog
area: auth
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
paperclip: POY-273
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Add Ktor bearer refresh and token rotation tests #repo/ratatoskr-client #area/auth #status/backlog ⏫ [paperclip:POY-273]

Filed from [POY-255](/POY/issues/POY-255) QA gate (row C10).

## Objective

Cover Ktor Auth bearer plugin behavior end-to-end so a 401 triggers a refresh against POST v1/auth/refresh, the new access + refresh tokens land in SecureStorage, the original request retries with the new bearer, and the response from the second attempt reaches the caller. Prove that a 5xx from refresh does not clear stored credentials and that 400/401/403 from refresh does.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New tests under core/data/src/commonTest/kotlin/com/po4yka/ratatoskr/data/remote/ exercising ApiClient + KtorAuthApi via MockEngine.
- Coverage cases: happy refresh on first 401; refresh failure with 5xx (tokens preserved); refresh failure with 401 (tokens cleared via existing shouldClearTokensAfterRefreshFailure predicate); single-flight refresh under concurrent in-flight requests.
- Run via: ./gradlew :core:data:allTests
- Wire-up: no new prod code required; tests should consume existing ApiClient factory.

## Constraints

- No live network. Use MockEngine.
- Do not weaken sanitizer guarantees already covered by ApiClientLogSanitizerTest.

## Definition of done

- Tests live in core/data commonTest, passing on Android JVM and iosSimulatorArm64.
- Tests fail meaningfully if Ktor Auth refresh wiring is removed or token rotation is bypassed.
- Linked back from this issue and from [POY-255](/POY/issues/POY-255) qa-gate document.
