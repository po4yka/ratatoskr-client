package com.po4yka.ratatoskr.util.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SecureStorageKeyValidatorTest {
    @Test
    fun `simple snake_case key is valid`() {
        assertTrue(SecureStorageKeyValidator.isValid("auth_token"))
    }

    @Test
    fun `kebab-case and dotted keys are valid`() {
        // The accepted alphabet is a-zA-Z0-9 plus `_`, `-`, `.` to
        // match the widest common subset across Android Tink/DataStore
        // and iOS Keychain key constraints.
        assertTrue(SecureStorageKeyValidator.isValid("auth-token"))
        assertTrue(SecureStorageKeyValidator.isValid("user.preferences"))
        assertTrue(SecureStorageKeyValidator.isValid("v1.access-token"))
    }

    @Test
    fun `empty key is rejected — degenerate input`() {
        assertFalse(SecureStorageKeyValidator.isValid(""))
    }

    @Test
    fun `keys with whitespace are rejected`() {
        // A space in a Keychain account name will silently round-trip
        // through some iOS APIs but trip up CSV exports and ADB shell
        // commands. Reject early.
        assertFalse(SecureStorageKeyValidator.isValid("auth token"))
        assertFalse(SecureStorageKeyValidator.isValid("auth\ttoken"))
        assertFalse(SecureStorageKeyValidator.isValid("auth\ntoken"))
    }

    @Test
    fun `keys with path separators are rejected`() {
        assertFalse(SecureStorageKeyValidator.isValid("auth/token"))
        assertFalse(SecureStorageKeyValidator.isValid("auth\\token"))
    }

    @Test
    fun `keys with shell metacharacters are rejected`() {
        // Pin the boundary — these can otherwise leak into logs and
        // confuse downstream tooling.
        assertFalse(SecureStorageKeyValidator.isValid("auth\$token"))
        assertFalse(SecureStorageKeyValidator.isValid("auth;token"))
        assertFalse(SecureStorageKeyValidator.isValid("auth|token"))
        assertFalse(SecureStorageKeyValidator.isValid("auth&token"))
    }

    @Test
    fun `keys longer than the max length are rejected`() {
        val tooLong = "x".repeat(SecureStorageKeyValidator.MAX_KEY_LENGTH + 1)
        assertFalse(SecureStorageKeyValidator.isValid(tooLong))
    }

    @Test
    fun `keys exactly at the max length boundary are valid`() {
        val justRight = "x".repeat(SecureStorageKeyValidator.MAX_KEY_LENGTH)
        assertTrue(SecureStorageKeyValidator.isValid(justRight))
    }

    @Test
    fun `normalize lowercases and substitutes invalid chars`() {
        // Round-tripping a user-supplied label (e.g. an account name)
        // into a storage key must succeed without throwing. Forbidden
        // chars become underscores.
        assertEquals(
            "user_email",
            SecureStorageKeyValidator.normalize("user@email"),
        )
        assertEquals(
            "auth_token",
            SecureStorageKeyValidator.normalize("auth/token"),
        )
        assertEquals(
            "v1_2_access",
            SecureStorageKeyValidator.normalize("v1 2/access"),
        )
    }

    @Test
    fun `normalize preserves valid chars dot dash underscore`() {
        assertEquals(
            "v1.2-access_key",
            SecureStorageKeyValidator.normalize("v1.2-access_key"),
        )
    }

    @Test
    fun `normalize lowercases uppercase input`() {
        // Match the round-trip behavior of common keystore impls that
        // case-fold lookups; lowercase canonicalization avoids the
        // "Auth_Token" vs "auth_token" duplicate-key surprise.
        assertEquals(
            "authtoken",
            SecureStorageKeyValidator.normalize("AUTHTOKEN"),
        )
        assertEquals(
            "auth_token",
            SecureStorageKeyValidator.normalize("Auth_Token"),
        )
    }

    @Test
    fun `normalize trims to the max length`() {
        val tooLong = "a".repeat(SecureStorageKeyValidator.MAX_KEY_LENGTH + 10)
        val normalized = SecureStorageKeyValidator.normalize(tooLong)
        assertEquals(SecureStorageKeyValidator.MAX_KEY_LENGTH, normalized?.length)
    }

    @Test
    fun `normalize returns null for null or empty input`() {
        assertNull(SecureStorageKeyValidator.normalize(null))
        assertNull(SecureStorageKeyValidator.normalize(""))
    }

    @Test
    fun `normalize is idempotent — normalize(normalize(x)) == normalize(x)`() {
        // Critical: a key that's been stored must produce the same
        // lookup key when re-normalized at retrieval time.
        val inputs =
            listOf(
                "auth_token",
                "Some User@Name",
                "Path/With/Slashes",
                "  spaced  ",
            )
        inputs.forEach { input ->
            val once = SecureStorageKeyValidator.normalize(input)
            val twice = once?.let { SecureStorageKeyValidator.normalize(it) }
            assertEquals(once, twice, "normalize is not idempotent for '$input'")
        }
    }

    @Test
    fun `normalize output is always a valid key when non-null`() {
        // Pin the round-trip invariant: a non-null normalize result
        // must satisfy isValid. If this ever breaks, the rest of the
        // codebase will start producing keys that fail their own
        // validator.
        val inputs =
            listOf(
                "user@email",
                "Auth_Token",
                "weird input/with#bad?chars",
                "v1.2-access",
            )
        inputs.forEach { input ->
            val normalized = SecureStorageKeyValidator.normalize(input)
            if (normalized != null) {
                assertTrue(
                    SecureStorageKeyValidator.isValid(normalized),
                    "normalize('$input')='$normalized' failed isValid",
                )
            }
        }
    }
}
