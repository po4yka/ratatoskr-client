package com.po4yka.ratatoskr.util.url

/**
 * Normalizes a user-pasted URL for the Submit-URL screen and the
 * clipboard-suggestion banner. The submit pipeline rejects:
 *  - cleartext `http://` URLs (the backend refuses them)
 *  - empty / multi-word pastes (search-query confusion)
 *  - dangerous schemes (`javascript:`, `data:`, `file:`)
 *  - schemes the app doesn't handle (`ftp:`, `mailto:`)
 *
 * `normalize(raw: String?): Result` returns one of:
 *  - [Result.Normalized] with a canonical `https://...` or
 *    `ratatoskr://...` URL — the only two schemes the app submits.
 *    `http://` is silently upgraded to `https://` so the user gets a
 *    successful summary rather than a confusing network error.
 *  - [Result.Unsupported] when the input has a scheme the app refuses
 *    on policy or security grounds.
 *  - [Result.Invalid] when the input is empty, contains whitespace,
 *    or doesn't look like a URL at all (no scheme and no dot).
 *
 * Scheme detection is case-insensitive (`HTTPS://...` parses) and
 * tolerates `host:port` confusion — a colon-prefix containing a dot
 * is treated as a host:port pair rather than a scheme.
 *
 * Pure, side-effect-free.
 */
object SubmittedUrlNormalizer {
    sealed interface Result {
        data class Normalized(val url: String) : Result

        data object Unsupported : Result

        data object Invalid : Result
    }

    private val SCHEME_PATTERN = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*$")

    fun normalize(raw: String?): Result {
        val trimmed = raw?.trim().orEmpty()
        if (trimmed.isEmpty()) return Result.Invalid
        if (trimmed.any { it.isWhitespace() }) return Result.Invalid

        val colonIdx = trimmed.indexOf(':')
        if (colonIdx > 0) {
            val prefix = trimmed.substring(0, colonIdx)
            if (prefix.matches(SCHEME_PATTERN) && '.' !in prefix) {
                val scheme = prefix.lowercase()
                val rest = trimmed.substring(colonIdx + 1).removePrefix("//")
                return when (scheme) {
                    "https", "http" -> Result.Normalized("https://$rest")
                    "ratatoskr" -> Result.Normalized("ratatoskr://$rest")
                    else -> Result.Unsupported
                }
            }
        }
        if ('.' !in trimmed) return Result.Invalid
        return Result.Normalized("https://$trimmed")
    }
}
