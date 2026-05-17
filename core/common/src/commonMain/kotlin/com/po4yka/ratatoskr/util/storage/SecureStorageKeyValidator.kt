package com.po4yka.ratatoskr.util.storage

/**
 * Pure key-shape predicate + normalizer for secure-storage entries.
 *
 * Accepted alphabet: `a-z`, `A-Z`, `0-9`, `_`, `-`, `.` — the widest
 * common subset across Android Tink/DataStore (which permits most
 * non-whitespace strings) and iOS Keychain account names (which round
 * trip cleanly for ASCII-identifier-shaped values). Anything else
 * (whitespace, path separators, shell metacharacters, unicode
 * punctuation) is rejected so a hand-crafted key from a user-supplied
 * label cannot smuggle in a quote, slash, or null byte that would
 * disrupt downstream tooling (logs, CSV exports, ADB shell commands).
 *
 * Length is capped at [MAX_KEY_LENGTH] to keep keys legible in logs and
 * within practical Keychain limits.
 *
 * [normalize] case-folds to lowercase so a user-supplied label
 * (`Auth_Token` and `auth_token`) doesn't accidentally produce two
 * distinct storage entries.
 *
 * Pure, side-effect-free, deterministic, and idempotent —
 * `normalize(normalize(x)) == normalize(x)` for every input.
 */
object SecureStorageKeyValidator {
    const val MAX_KEY_LENGTH: Int = 128

    fun isValid(key: String): Boolean {
        if (key.isEmpty()) return false
        if (key.length > MAX_KEY_LENGTH) return false
        return key.all { isAllowedChar(it) }
    }

    fun normalize(rawKey: String?): String? {
        if (rawKey.isNullOrEmpty()) return null
        val mapped =
            rawKey
                .lowercase()
                .map { if (isAllowedChar(it)) it else '_' }
                .joinToString("")
                .take(MAX_KEY_LENGTH)
        return mapped.ifEmpty { null }
    }

    private fun isAllowedChar(ch: Char): Boolean =
        ch.isLetterOrDigitAscii() || ch == '_' || ch == '-' || ch == '.'

    private fun Char.isLetterOrDigitAscii(): Boolean = (this in '0'..'9') || (this in 'a'..'z') || (this in 'A'..'Z')
}
