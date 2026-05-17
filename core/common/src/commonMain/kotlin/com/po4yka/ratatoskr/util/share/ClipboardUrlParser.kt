package com.po4yka.ratatoskr.util.share

/**
 * Shared `http(s)://` URL extractor used by every Android entry point
 * that consumes free-form clipboard or share-sheet text:
 *
 * - [com.po4yka.ratatoskr.MainActivity]'s `ACTION_SEND` handler
 * - the Quick Settings tile that submits the current clipboard URL
 * - any future iOS share-extension or shortcut hook
 *
 * The regex is conservative — only `http://` and `https://` are
 * recognised, so `ftp://` and `mailto:` text never end up in the
 * submission flow. Trailing sentence punctuation is stripped because
 * the most common clipboard shape is "Check this out: <url>." and the
 * "." would otherwise be part of the captured URL.
 */
object ClipboardUrlParser {
    private val HTTP_URL_REGEX = Regex("""https?://[^\s<>"']+""", RegexOption.IGNORE_CASE)
    private const val TRAILING_PUNCTUATION = ".,;:)]}"

    /**
     * Returns the first http(s) URL in [text], with trailing
     * `.,;:)]}` punctuation trimmed. Returns `null` when [text] is
     * blank or contains no http(s) URL.
     */
    fun firstHttpUrl(text: String): String? =
        HTTP_URL_REGEX
            .find(text)
            ?.value
            ?.trimEnd { it in TRAILING_PUNCTUATION }
}
