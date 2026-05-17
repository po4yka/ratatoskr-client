---
title: Unify HTTP client refresh via shared TokenRefresher
status: backlog
area: auth
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Unify HTTP client refresh via shared TokenRefresher #repo/ratatoskr-client #area/auth #status/backlog 🔺

Filed from the 2026-05-17 deep audit (cross-cutting finding CC1).

## Objective

Eliminate the refresh-token race between the two Ktor clients. Today `core/data/.../data/remote/ApiClient.kt:112,200-272` and `composeApp/.../di/GeneratedApiInitializer.kt:16,48-79` each hold a private `Mutex` while sharing one `SecureStorage`. Concurrent 401s can drive two `POST v1/auth/refresh` requests with the same refresh token; the server rejects the second as reused and the loser path clears tokens → silent logout. The generated client also has no reactive 401 refresh (only proactive JWT expiry check) so stale tokens that sneak through return errors to callers permanently until restart.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `TokenRefresher` singleton in `core/data` holding one `Mutex`, an in-flight `Deferred<BearerTokens?>` cache, and a single `dataStore.edit { ... }` write that persists access + refresh atomically.
- `ApiClient.kt` `bearer { refreshTokens { ... } }` delegates to the refresher.
- `GeneratedApiInitializer.kt` installs the `Auth` plugin (or an equivalent 401 retry interceptor) routed through the same refresher.
- `feature/auth/.../AuthRepositoryImpl.logoutWithRevoke` cancels in-flight refreshes (suppress-write flag) before clearing tokens.
- Refresh `POST v1/auth/refresh` opted out of the bearer pipeline so it cannot recurse on 401.

## Constraints

- Must keep the existing `shouldClearTokensAfterRefreshFailure` predicate (`ApiClient.kt:255-263`) — 5xx preserves tokens, 4xx clears.
- Atomic two-token save on Android (single `DataStore.edit`) and iOS (single `settings.transaction { }`).
- No behavior change to log sanitization (`ApiClientLogSanitizerTest` must still pass).

## Definition of done

- One mutex governs all refresh attempts process-wide.
- Crash between access-save and refresh-save is impossible (atomic write verified by test).
- New tests (extending the open Ktor-refresh task `add-ktor-bearer-refresh-and-token-rotation-tests`) cover concurrent generated + hand-written calls observing 401 and resolve to one shared refresh.
- No more dual-mutex code paths in the repo.
