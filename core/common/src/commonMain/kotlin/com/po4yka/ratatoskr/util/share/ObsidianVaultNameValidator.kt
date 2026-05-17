package com.po4yka.ratatoskr.util.share

/**
 * Validates a user-entered Obsidian vault name before it is composed
 * into the `obsidian://new?vault=...&name=...&content=...` deep-link.
 * The existing `ObsidianDeepLink.composeNewNote` percent-encodes the
 * parameter, so encoding safety is not at risk — but vault names that
 * contain path separators or reserved filesystem characters silently
 * route the new note into the wrong vault (or no vault) on Obsidian's
 * side. The Settings -> Sharing -> "Obsidian vault" field consumes
 * this validator before persisting the value.
 *
 * Rules:
 *  - null, empty, and whitespace-only input collapse to [Result.Empty]
 *    so the deep-link omits the vault parameter and Obsidian opens the
 *    last-active vault.
 *  - surrounding whitespace is trimmed silently.
 *  - `/` and `\` are rejected as path separators
 *    ([Reason.ContainsPathSeparator]).
 *  - `:` is rejected as a reserved character on Windows and legacy macOS
 *    ([Reason.ContainsReservedChar]).
 *  - any ISO C0 control character (NUL, LF, CR, TAB, etc.) and DEL is
 *    rejected as a defense against pasted CRLF and injection-shaped
 *    input ([Reason.ContainsControlChar]).
 *  - names longer than [MAX_LENGTH] (after trim) are rejected
 *    ([Reason.ExceedsMaxLength]).
 *
 * Unicode letters and digits — Cyrillic, CJK, emoji — are accepted; the
 * vault is rendered as a folder on disk and modern filesystems support
 * UTF-8 names.
 */
object ObsidianVaultNameValidator {
    const val MAX_LENGTH = 200

    enum class Reason {
        ContainsPathSeparator,
        ContainsReservedChar,
        ContainsControlChar,
        ExceedsMaxLength,
    }

    sealed interface Result {
        data object Empty : Result

        data class Valid(val name: String) : Result

        data class Invalid(val reason: Reason) : Result
    }

    fun validate(raw: String?): Result {
        val trimmed = raw?.trim().orEmpty()
        if (trimmed.isEmpty()) return Result.Empty
        for (ch in trimmed) {
            when {
                ch == '/' || ch == '\\' -> return Result.Invalid(Reason.ContainsPathSeparator)
                ch == ':' -> return Result.Invalid(Reason.ContainsReservedChar)
                isControl(ch) -> return Result.Invalid(Reason.ContainsControlChar)
            }
        }
        if (trimmed.length > MAX_LENGTH) return Result.Invalid(Reason.ExceedsMaxLength)
        return Result.Valid(trimmed)
    }

    private fun isControl(ch: Char): Boolean = ch.code < 0x20 || ch.code == 0x7F
}
