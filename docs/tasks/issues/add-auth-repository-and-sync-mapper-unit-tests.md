---
title: Add AuthRepositoryImpl and SyncMapper unit tests
status: backlog
area: testing
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add AuthRepositoryImpl and SyncMapper unit tests #repo/ratatoskr-client #area/testing #status/backlog ⏫

Filed from the 2026-05-17 deep audit (test gaps #2 and #4).

## Objective

Two high-risk untested classes:

1. `feature/auth/.../data/repository/AuthRepositoryImpl.kt` — `getCurrentUser` error branch (lines 117-123) calls `invalidateSession()` on **any** non-cancellation exception, silently logging the user out on a transient network blip. Also `checkAuthStatus`, `logout`, `loginWithSecret` token-skip branches untested.
2. `feature/sync/.../SyncMapper.kt` `JsonObject.toSummaryEntity` (lines 47-129) parses 12+ nullable backend fields with a `tldr → summary_250 → ""` fallback chain and a try/catch around `createdAt` parsing that silently substitutes `Clock.System.now()`.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `feature/auth/src/commonTest/kotlin/.../AuthRepositoryImplTest.kt` using a fake `SecureStorage`:
  - `checkAuthStatus emits false when no access token stored`
  - `checkAuthStatus emits true after token saved`
  - `logout clears tokens and sets isAuthenticated to false`
  - `getCurrentUser invalidates session on unexpected API exception`
  - `logoutWithRevoke calls local logout even when server revoke throws`
- New `feature/sync/src/commonTest/kotlin/.../SyncMapperTest.kt`:
  - `toSummaryEntity returns null when payload is null`
  - `toSummaryEntity uses tldr when summary_250 absent`
  - `toSummaryEntity uses summary_250 when both fields present`
  - `toSummaryEntity falls back to Clock.now() for malformed createdAt`
  - `toSummaryEntity defaults boolean fields to false when absent`

## Constraints

- No live network, no Koin. Pure unit tests.
- Fake `SecureStorage` must not be reused across tests (fresh per test).

## Definition of done

- All cases pass on `iosSimulatorArm64Test` + Android JVM.
- A regression in `invalidateSession()` behavior fails the test.
