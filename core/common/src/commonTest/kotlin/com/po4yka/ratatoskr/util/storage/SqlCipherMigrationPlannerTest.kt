package com.po4yka.ratatoskr.util.storage

import com.po4yka.ratatoskr.util.storage.SqlCipherMigrationPlanner.Action
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlCipherMigrationPlannerTest {
    @Test
    fun `fresh install with no databases produces CreateEncrypted`() {
        // First launch on a new device — no plain DB, no encrypted DB, key
        // status immaterial.
        val plan =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = false,
                encryptedDbExists = false,
                encryptedKeyAvailable = false,
            )
        assertEquals(Action.CreateEncrypted, plan)

        val withKey =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = false,
                encryptedDbExists = false,
                encryptedKeyAvailable = true,
            )
        assertEquals(Action.CreateEncrypted, withKey)
    }

    @Test
    fun `pre-encryption install with only a plain DB produces EncryptExistingPlaintext`() {
        // Existing users upgrading from a pre-encryption build. The migration
        // generates a key (or reuses one if present), encrypts the existing
        // tables, then deletes the plain file. The decision is the same with
        // or without a pre-existing key.
        val withoutKey =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = true,
                encryptedDbExists = false,
                encryptedKeyAvailable = false,
            )
        assertEquals(Action.EncryptExistingPlaintext, withoutKey)

        val withKey =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = true,
                encryptedDbExists = false,
                encryptedKeyAvailable = true,
            )
        assertEquals(Action.EncryptExistingPlaintext, withKey)
    }

    @Test
    fun `encrypted-only happy path with usable key produces OpenEncrypted`() {
        // Steady state: previous launch migrated, key is in Tink AEAD storage,
        // ciphertext DB is on disk. Open it.
        val plan =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = false,
                encryptedDbExists = true,
                encryptedKeyAvailable = true,
            )
        assertEquals(Action.OpenEncrypted, plan)
    }

    @Test
    fun `encrypted DB with lost key produces WipeAndResync — fail-safe`() {
        // Spec: "fail-safe wipe + re-sync on migration error." If the AEAD key
        // is gone (Tink rotated, DataStore corruption, user cleared app data
        // partially), the ciphertext is unrecoverable; we cannot reuse it.
        val plan =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = false,
                encryptedDbExists = true,
                encryptedKeyAvailable = false,
            )
        assertEquals(Action.WipeAndResync, plan)
    }

    @Test
    fun `both plain and encrypted DBs present is treated as corrupt — WipeAndResync`() {
        // An interrupted migration (process killed mid-encrypt) leaves both
        // files on disk. Their data could disagree silently; the safe move
        // is to wipe both and re-sync the latest snapshot from the server.
        // The user-modified-but-unsynced state is the only loss, and the
        // server-side sync already preserves committed work.
        val withKey =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = true,
                encryptedDbExists = true,
                encryptedKeyAvailable = true,
            )
        assertEquals(Action.WipeAndResync, withKey)

        val withoutKey =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = true,
                encryptedDbExists = true,
                encryptedKeyAvailable = false,
            )
        assertEquals(Action.WipeAndResync, withoutKey)
    }

    @Test
    fun `EncryptExistingPlaintext takes precedence over OpenEncrypted only when plain alone exists`() {
        // Defends the precedence: a partial state where both files exist is
        // ambiguous, not "migrate again". Pins the rule that the migration
        // path only fires when there is no encrypted DB to fall back to.
        val migrateOnly =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = true,
                encryptedDbExists = false,
                encryptedKeyAvailable = true,
            )
        assertEquals(Action.EncryptExistingPlaintext, migrateOnly)

        val ambiguous =
            SqlCipherMigrationPlanner.plan(
                plainDbExists = true,
                encryptedDbExists = true,
                encryptedKeyAvailable = true,
            )
        assertEquals(Action.WipeAndResync, ambiguous)
    }

    @Test
    fun `the action enum enumerates exactly the four expected outcomes`() {
        // Pins the contract surface so a refactor that adds a new outcome
        // (e.g. "MigrateToNewKey") must update tests deliberately.
        assertEquals(4, Action.entries.size)
        val expected =
            setOf(
                Action.CreateEncrypted,
                Action.EncryptExistingPlaintext,
                Action.OpenEncrypted,
                Action.WipeAndResync,
            )
        assertEquals(expected, Action.entries.toSet())
    }
}
