package com.po4yka.ratatoskr.util.storage

/**
 * Pure pre-write guard for secure-storage values (bearer tokens, refresh
 * tokens, encrypted blobs). Complements [SecureStorageKeyValidator] —
 * the key guard prevents key collisions and tooling breakage, this one
 * prevents value-side foot-guns:
 *
 *  - [Outcome.RejectNull]         — null write would either NPE on iOS
 *                                    Keychain bridges or be misinterpreted
 *                                    as a clear-and-delete. Reject and
 *                                    let the caller decide.
 *  - [Outcome.RejectInvalidChar]  — embedded null byte (0x00) or other
 *                                    ISO control codes (<0x20 except
 *                                    `\t` and `\n`, plus 0x7F DEL) which
 *                                    truncate or corrupt downstream.
 *  - [Outcome.RejectTooLarge]     — beyond [MAX_VALUE_LENGTH], which is
 *                                    the conservative iOS Keychain item
 *                                    size limit.
 *  - [Outcome.Accept]             — write may proceed.
 *
 * The empty string is **accepted** because it's the canonical "clear me"
 * sentinel used by `AuthRepositoryImpl.logoutWithRevoke`; the distinction
 * between null and empty matters at the call site.
 *
 * Pure, side-effect-free, deterministic.
 */
object SecureStorageValueGuard {
    enum class Outcome {
        Accept,
        RejectNull,
        RejectInvalidChar,
        RejectTooLarge,
    }

    const val MAX_VALUE_LENGTH: Int = 4096

    fun check(value: String?): Outcome {
        if (value == null) return Outcome.RejectNull
        if (value.length > MAX_VALUE_LENGTH) return Outcome.RejectTooLarge
        for (ch in value) {
            if (isInvalidControl(ch)) return Outcome.RejectInvalidChar
        }
        return Outcome.Accept
    }

    private fun isInvalidControl(ch: Char): Boolean {
        val code = ch.code
        if (code == 0x09 || code == 0x0A || code == 0x0D) return false
        return code < 0x20 || code == 0x7F
    }
}
