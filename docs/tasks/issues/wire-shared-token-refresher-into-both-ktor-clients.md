---
title: Wire SharedTokenRefresher into ApiClient + GeneratedApiInitializer with TokenStorage actual
status: backlog
area: auth
priority: critical
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire SharedTokenRefresher into ApiClient + GeneratedApiInitializer with TokenStorage actual #repo/ratatoskr-client #area/auth #status/backlog 🔺

Follow-up to `unify-http-client-refresh-via-shared-token-refresher` (landed the durable testable atom: `core/data/.../data/remote/auth/SharedTokenRefresher.kt` with a single `Mutex`, the "did another caller refresh during my wait" re-check that mirrors the existing `ApiClient.kt:233` predicate, and the atomic two-token `saveTokens` contract. 5 commonTest cases cover the success path, hard/soft failure semantics, stale-token short-circuit, and the dual-mutex regression — 5 concurrent callers see exactly one HTTP refresh fire).

## Objective

Replace the existing two refresh paths with the shared singleton:

1. **`TokenStorage` adapter** wrapping the existing `SecureStorage` so its `saveTokens(BearerPair)` is implemented as a single `DataStore.edit { }` on Android and a single `settings.transaction { }` on iOS — atomic writes, no half-saved pair.
2. **`ApiClient.kt`** — replace the private `tokenRefreshMutex` + `refreshTokens { ... }` block with a delegate that calls `sharedTokenRefresher.refresh(oldTokens?.accessToken)` and translates the result to `BearerTokens?`.
3. **`GeneratedApiInitializer.kt`** — install the same `Auth` plugin (or an equivalent 401 retry interceptor) routed through the same `SharedTokenRefresher` instance.
4. **`feature/auth/.../AuthRepositoryImpl.logoutWithRevoke`** — set a "suppress write" flag on the refresher (or simply call `clearTokens()` on storage and let the next refresh attempt return null) before clearing tokens, so a refresh racing with logout doesn't re-populate tokens after the logout.
5. **`POST v1/auth/refresh`** opted out of the bearer pipeline so it cannot recurse on its own 401.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/data/.../data/remote/auth/SecureStorageTokenStorage.kt` — `SecureStorage`-backed `TokenStorage` actual with atomic write in `saveTokens`.
- `core/data/.../data/remote/ApiClient.kt` refactored to delegate.
- `composeApp/.../di/GeneratedApiInitializer.kt` refactored to install Ktor `Auth` plugin routed through the same refresher.
- DI: `SharedTokenRefresher` registered as `@Single`; both clients depend on it.
- `feature/auth/.../AuthRepositoryImpl.kt` logout flow validated to no longer race.
- Integration tests extending `add-ktor-bearer-refresh-and-token-rotation-tests` to cover concurrent generated + hand-written calls observing 401 and resolving to one shared refresh.

## Constraints

- Preserve the existing `shouldClearTokensAfterRefreshFailure(status)` predicate (5xx preserves tokens, 4xx clears) — already encoded in the refresher as `HardFailure` vs `SoftFailure`.
- Atomic two-token save on Android (single `DataStore.edit { }`) and iOS (single `settings.transaction { }`). The `TokenStorage.saveTokens` contract test will fail otherwise.
- No behavior change to log sanitization — `ApiClientLogSanitizerTest` must still pass after the refactor.

## Definition of done

- One mutex governs all refresh attempts process-wide.
- Crash between access-save and refresh-save is impossible (atomic write verified by test).
- Integration tests cover concurrent generated + hand-written calls and verify only one HTTP refresh fires per 401 burst.
- No more dual-mutex code paths in the repo.
