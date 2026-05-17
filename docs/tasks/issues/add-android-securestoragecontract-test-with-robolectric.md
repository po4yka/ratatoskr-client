---
title: Add Android SecureStorageContract test with Robolectric setup
status: backlog
area: auth
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android SecureStorageContract test with Robolectric setup #repo/ratatoskr-client #area/auth #status/backlog ⏫

Follow-up to `add-android-securestorage-robolectric-test-with-aead-key-reuse` (landed the contract-level support for AEAD key-reuse coverage: extended `SecureStorageContract` with a `recreateAgainstSameStore(existing)` open hook plus two new test methods — token round-trip + dev-credentials round-trip across `SecureStorage` recreation against the same backing store. iOS now overrides the hook to give the cross-instance Keychain-reuse assertions real coverage; desktop returns null and the tests skip cleanly).

## Objective

Wire the Android lane so it gets the same AEAD-keyset-reuse coverage iOS now has.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Add Robolectric + androidx.test.core to the version catalog and to `core/data/build.gradle.kts`'s `androidHostTest.dependencies` block (currently only has mockk):
  ```toml
  # libs.versions.toml
  robolectric = "4.13"
  androidx-test-core = "1.6.1"
  ```
- New `core/data/src/androidHostTest/kotlin/com/po4yka/ratatoskr/data/local/AndroidSecureStorageTest.kt` subclassing `SecureStorageContract`:
  - `createStorage()` builds an `AndroidSecureStorage` against `ApplicationProvider.getApplicationContext()` with a unique `dataStoreName` per test method to keep state isolated.
  - `recreateAgainstSameStore(existing)` builds a second `AndroidSecureStorage` against the **same context** and the **same `dataStoreName`** — the recreation hook the contract already calls. The inherited cross-instance tests then exercise Tink-AEAD-key reuse for free.
- `@RunWith(AndroidJUnit4::class)` (Robolectric resolves it automatically) on the test class.
- Optional: a direct-read assertion that opens the underlying `DataStore<Preferences>` and confirms the persisted value is **not** the plaintext token — proves the encrypted-vs-plaintext fallback hasn't slipped on.

## Constraints

- Robolectric brings transitive overhead — keep the test class focused on the contract + the AEAD-key-reuse direct-read assertion.
- Tests must not leak DataStore state between runs: either use a unique `dataStoreName` per test method, or wipe the data dir in `@After`.
- The contract's `recreateAgainstSameStore` is already in place — the Android subclass just has to override it correctly.

## Definition of done

- `./gradlew :core:data:testAndroidHostTest` runs the contract + the new AEAD-reuse assertions on the Android lane.
- Test fails if the Tink keyset is regenerated per `AndroidSecureStorage` instance (the inherited cross-instance round-trip would read empty).
- Test fails if a token is written to a plaintext DataStore fallback (the direct-read assertion would see the plaintext).
