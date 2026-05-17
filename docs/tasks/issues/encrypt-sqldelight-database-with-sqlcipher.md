---
title: Encrypt SQLDelight database with SQLCipher (Android) and NSFileProtection (iOS)
status: backlog
area: db
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Encrypt SQLDelight database with SQLCipher (Android) and NSFileProtection (iOS) #repo/ratatoskr-client #area/db #status/backlog ⏫

Filed from the 2026-05-17 deep audit (security M6).

## Objective

`core/data/src/androidMain/.../DatabaseDriverFactory.kt:11` uses a plain `AndroidSqliteDriver`. Same story on iOS via the native driver. Summaries, collections, sync state, and user content cached locally sit in cleartext SQLite — readable by `adb backup` on rooted devices and by malicious apps via confused-deputy bugs in `FileProvider`. MASVS-STORAGE-1.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Android: integrate `net.zetetic:android-database-sqlcipher` via `app.cash.sqldelight:android-driver`'s `SupportFactory`. Passphrase generated once via `SecureRandom`, stored under Tink AEAD in DataStore (reuse the existing AEAD key from `TinkKeyManager.kt`).
- iOS: enable SQLite file protection class `NSFileProtectionCompleteUntilFirstUserAuthentication` on the database file. (Consider GRDB+SQLCipher if file-protection alone is insufficient.)
- Migration path for installed users: detect unencrypted DB, re-encrypt with new passphrase, fail-safe wipe + re-sync on migration error.

## Constraints

- Performance budget: queries on warm DB must stay within 2× of plain SQLite.
- Migration must not lose user-modified-but-unsynced state.

## Definition of done

- DB file on Android rooted device shows no plaintext summaries.
- DB file on iOS unreadable while device locked.
- Migration tested with synthetic pre-encryption state.
