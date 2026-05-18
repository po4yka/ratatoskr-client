package com.po4yka.ratatoskr.util.storage

import kotlin.test.Test
import kotlin.test.assertEquals

class SqlCipherPassphraseSpecTest {
    @Test
    fun `32 bytes — Accept`() {
        // Pin the canonical SQLCipher passphrase byte length. SQLCipher 4
        // defaults to 256-bit (32-byte) keys; the Android applier passes
        // the bytes straight into SupportFactory(passphrase) without a
        // KDF, so the validator must enforce the size exactly.
        val key = ByteArray(SqlCipherPassphraseSpec.PASSPHRASE_BYTES) { 0x42 }
        assertEquals(
            SqlCipherPassphraseSpec.Outcome.Accept,
            SqlCipherPassphraseSpec.validate(key),
        )
    }

    @Test
    fun `null bytes — Missing`() {
        // DataStore returns null for an absent key blob. The planner
        // already classifies that as a "no key" state, but the validator
        // distinguishes null from a present-but-malformed value so the
        // caller can route between EncryptExistingPlaintext (no key) and
        // WipeAndResync (corrupt key).
        assertEquals(
            SqlCipherPassphraseSpec.Outcome.Missing,
            SqlCipherPassphraseSpec.validate(bytes = null),
        )
    }

    @Test
    fun `empty bytes — TooShort`() {
        assertEquals(
            SqlCipherPassphraseSpec.Outcome.TooShort,
            SqlCipherPassphraseSpec.validate(ByteArray(0)),
        )
    }

    @Test
    fun `31 bytes — TooShort`() {
        assertEquals(
            SqlCipherPassphraseSpec.Outcome.TooShort,
            SqlCipherPassphraseSpec.validate(ByteArray(31)),
        )
    }

    @Test
    fun `33 bytes — TooLong`() {
        // Distinguish from TooShort so the caller can log the exact
        // failure shape without a length round-trip.
        assertEquals(
            SqlCipherPassphraseSpec.Outcome.TooLong,
            SqlCipherPassphraseSpec.validate(ByteArray(33)),
        )
    }

    @Test
    fun `64 bytes — TooLong`() {
        assertEquals(
            SqlCipherPassphraseSpec.Outcome.TooLong,
            SqlCipherPassphraseSpec.validate(ByteArray(64)),
        )
    }

    @Test
    fun `validate is content-independent — only length matters`() {
        // The validator is a length check, not a strength check. All-zero
        // bytes are not a security problem at this layer — the AEAD key
        // wraps the passphrase blob; the threat model is "device disk
        // dump", not "predictable passphrase".
        val zeros = ByteArray(SqlCipherPassphraseSpec.PASSPHRASE_BYTES) { 0 }
        val ones = ByteArray(SqlCipherPassphraseSpec.PASSPHRASE_BYTES) { 0xFF.toByte() }
        assertEquals(SqlCipherPassphraseSpec.Outcome.Accept, SqlCipherPassphraseSpec.validate(zeros))
        assertEquals(SqlCipherPassphraseSpec.Outcome.Accept, SqlCipherPassphraseSpec.validate(ones))
    }

    @Test
    fun `validate is deterministic`() {
        val key = ByteArray(SqlCipherPassphraseSpec.PASSPHRASE_BYTES) { it.toByte() }
        val a = SqlCipherPassphraseSpec.validate(key)
        val b = SqlCipherPassphraseSpec.validate(key)
        assertEquals(a, b)
    }
}
