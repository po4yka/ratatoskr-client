package com.po4yka.ratatoskr.util.storage

/**
 * Pure decision atom for the SQLCipher migration path. The bootstrap caller
 * (`DatabaseDriverFactory` on Android, the iOS equivalent) probes three
 * filesystem / keystore booleans and asks this planner which of the four
 * canonical actions to perform.
 *
 * The truth table:
 *
 * | plain DB | encrypted DB | key | → action |
 * |---|---|---|---|
 * | F | F | _ | CreateEncrypted |
 * | T | F | _ | EncryptExistingPlaintext |
 * | F | T | T | OpenEncrypted |
 * | F | T | F | WipeAndResync (key lost) |
 * | T | T | _ | WipeAndResync (corrupt — interrupted migration) |
 *
 * The two corrupt-state branches are deliberate fail-safes per the spec —
 * "fail-safe wipe + re-sync on migration error" — rather than attempts to
 * reconstruct intent from partial state. The only loss is user-modified
 * but unsynced state; the server-side sync already preserves committed work
 * for any data that mattered.
 */
object SqlCipherMigrationPlanner {
    enum class Action {
        CreateEncrypted,
        EncryptExistingPlaintext,
        OpenEncrypted,
        WipeAndResync,
    }

    fun plan(
        plainDbExists: Boolean,
        encryptedDbExists: Boolean,
        encryptedKeyAvailable: Boolean,
    ): Action =
        when {
            plainDbExists && encryptedDbExists -> Action.WipeAndResync
            plainDbExists -> Action.EncryptExistingPlaintext
            encryptedDbExists && encryptedKeyAvailable -> Action.OpenEncrypted
            encryptedDbExists -> Action.WipeAndResync
            else -> Action.CreateEncrypted
        }
}
