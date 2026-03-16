package com.po4yka.bitesizereader.util

/**
 * Strips potentially dangerous HTML tags from markdown content while preserving
 * safe structural tags that markdown renderers handle.
 */
object MarkdownSanitizer {
    private val DANGEROUS_TAG_PATTERN = Regex(
        "</?\\s*(script|iframe|object|embed|form|input|button|style|link|meta|base|applet)\\b[^>]*>",
        RegexOption.IGNORE_CASE,
    )

    fun sanitize(markdown: String): String {
        return DANGEROUS_TAG_PATTERN.replace(markdown, "")
    }
}
