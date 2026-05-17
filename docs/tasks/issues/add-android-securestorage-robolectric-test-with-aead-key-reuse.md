---
title: Add Android SecureStorage Robolectric test with AEAD key-reuse assertion
status: backlog
area: auth
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android SecureStorage Robolectric test with AEAD key-reuse assertion #repo/ratatoskr-client #area/auth #status/backlog ⏫

Follow-up to `add-securestorage-round-trip-and-aead-key-persistence-tests`. The shared `SecureStorageContract` and its desktop and iOS implementations landed; the Android lane is still uncovered because `AndroidSecureStorage` needs a real `Context` + `DataStore<Preferences>` and the repo has no Robolectric integration yet.

## Objective

Extend the `SecureStorageContract` to the Android lane and add one Tink-specific assertion that catches AEAD-key bypass regressions.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Wire Robolectric (`org.robolectric:robolectric` + `androidx.test.core`) into `core/data/build.gradle.kts`'s `androidHostTest.dependencies` block (currently only has mockk).
- New `core/data/src/androidHostTest/kotlin/.../AndroidSecureStorageTest.kt` subclassing `SecureStorageContract` with `Context = ApplicationProvider.getApplicationContext()` and a Robolectric `@RunWith(RobolectricTestRunner::class)` annotation if needed.
- Additional AEAD-specific test: write a token via one `AndroidSecureStorage` instance, recreate the instance against the same context, confirm the token reads back correctly (proves the Tink keyset is persisted, not regenerated). Recreate a second instance and confirm the token still reads — pins AEAD-key reuse across the storage object's lifecycle.
- Optional: read the raw DataStore entry directly and assert it is NOT the plaintext token (proves the encrypted-vs-plaintext fallback path).

## Constraints

- Robolectric brings transitive overhead; keep the test class focused.
- Tests must not leak DataStore state between runs (use a unique `dataStoreName` per test or wipe the data dir in `@After`).

## Definition of done

- `./gradlew :core:data:testAndroidHostTest` runs the contract + AEAD-reuse test.
- Test fails if the Tink keyset is regenerated per `AndroidSecureStorage` instance.
- Test fails if a token is written to a plaintext DataStore fallback.
