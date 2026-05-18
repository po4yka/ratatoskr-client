package com.po4yka.ratatoskr.util.storage

/**
 * Validation rules for the SQLCipher passphrase blob the Android applier
 * hands to `SupportFactory(passphrase)`. Companion to
 * [SqlCipherMigrationPlanner] — the planner decides which migration path
 * to take, this atom decides whether the bytes it was about to use are
 * shaped correctly.
 *
 * SQLCipher 4 defaults to 256-bit (32-byte) keys. The applier passes the
 * bytes straight in without a KDF, so the validator enforces an exact
 * length match. Out-of-bound shapes (too short, too long, missing) are
 * distinguished so the caller can branch — most importantly, a present-
 * but-malformed blob routes the planner to `WipeAndResync` while a
 * missing blob routes to `EncryptExistingPlaintext`.
 *
 * Pure, side-effect-free, deterministic. Content-independent: a 32-byte
 * key of all zeros is structurally valid here; the strength assertion
 * belongs at the generator (SecureRandom), not at the validator.
 */
object SqlCipherPassphraseSpec {
    const val PASSPHRASE_BYTES: Int = 32

    enum class Outcome {
        Accept,
        Missing,
        TooShort,
        TooLong,
    }

    fun validate(bytes: ByteArray?): Outcome {
        if (bytes == null) return Outcome.Missing
        return when {
            bytes.size < PASSPHRASE_BYTES -> Outcome.TooShort
            bytes.size > PASSPHRASE_BYTES -> Outcome.TooLong
            else -> Outcome.Accept
        }
    }
}
