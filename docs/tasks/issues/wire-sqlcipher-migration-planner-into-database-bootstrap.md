---
title: Wire SqlCipherMigrationPlanner into DatabaseDriverFactory bootstrap
status: backlog
area: db
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire SqlCipherMigrationPlanner into DatabaseDriverFactory bootstrap #repo/ratatoskr-client #area/db #status/backlog ⏫

Follow-up to `encrypt-sqldelight-database-with-sqlcipher` (landed the durable
testable atom:
`core/common/.../util/storage/SqlCipherMigrationPlanner.kt` — pure
3-boolean → 4-action decision atom for the SQLCipher migration path:
plain-only → EncryptExistingPlaintext, encrypted+key → OpenEncrypted,
encrypted-without-key → WipeAndResync (key lost), both-present →
WipeAndResync (corrupt / interrupted migration). 6 commonTest cases
pin every row of the truth table.).

## Objective

Bring up the platform integration that consumes the planner:

1. **Android passphrase + key storage**:
   - Generate a 32-byte passphrase via `SecureRandom`.
   - Encrypt with the existing `TinkKeyManager` AEAD key, store the
     ciphertext under a stable `sqlcipher_passphrase_v1` DataStore key.
   - Probe the three booleans the planner needs by stat-ing
     `getDatabasePath("ratatoskr.db")` (plain) and
     `getDatabasePath("ratatoskr-encrypted.db")` (encrypted) plus
     checking DataStore for the key blob.
2. **Android `SupportFactory` integration**:
   - Add `net.zetetic:android-database-sqlcipher` dependency.
   - On `EncryptExistingPlaintext`: open the plain DB, run
     `ATTACH DATABASE 'encrypted.db' AS encrypted KEY '<base64>'`,
     `SELECT sqlcipher_export('encrypted')`, then delete the plain file.
   - On `OpenEncrypted`: pass the decrypted passphrase as a `SupportFactory`
     in `AndroidSqliteDriver(SupportFactory(passphrase.toByteArray()))`.
   - On `WipeAndResync`: delete both DB files, generate a fresh
     passphrase, `CreateEncrypted`, then trigger a full sync.
3. **iOS file protection**:
   - Set `NSFileProtectionCompleteUntilFirstUserAuthentication` on the
     database file via `NSFileManager.setAttributes(_:ofItemAtPath:)`.
   - Optional follow-on: integrate GRDB+SQLCipher when the iOS team
     decides the file-protection class alone isn't sufficient.
4. **DI bindings** for the new `DatabasePassphraseProvider` actuals in
   `AndroidModule.kt`, `IosModule.kt`, `DesktopModule.kt` (Desktop is a
   dev target — pass-through OK).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/data/src/commonMain/.../DatabaseDriverFactory.kt` — call
  `SqlCipherMigrationPlanner.plan(...)` and branch.
- Android: `androidMain` actual with SQLCipher `SupportFactory` +
  `sqlcipher_export` migration path.
- iOS: file-protection attribute application at first DB open.
- `gradle/libs.versions.toml` — new SQLCipher dependency.

## Constraints

- Performance budget: queries on warm DB must stay within 2× of plain
  SQLite (measured by the existing benchmark suite — to be added).
- Migration must not lose user-modified-but-unsynced state. Where
  WipeAndResync is the planner's verdict, the worst loss is the local
  pending-op queue, which the user can re-trigger by their next action.
- Never reuse a passphrase across reinstalls; the Tink AEAD key is
  cleared on uninstall, which is the correct boundary.

## Definition of done

- DB file on Android rooted device shows no plaintext summaries.
- DB file on iOS unreadable while device locked.
- Migration tested with synthetic pre-encryption state (plain DB present,
  no encrypted, no key → planner returns EncryptExistingPlaintext, the
  bootstrap finishes with encrypted DB on disk and no plain residue).
- Synthetic corrupt-state recovery tested (both DBs on disk → planner
  returns WipeAndResync, bootstrap wipes both and triggers full sync).
