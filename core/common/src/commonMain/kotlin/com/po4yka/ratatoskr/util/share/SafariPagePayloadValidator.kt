package com.po4yka.ratatoskr.util.share

/**
 * Validated page-context payload from the iOS Safari Action Extension.
 * Carries the visible page title (may be empty if Safari omits it) and
 * the source URL (guaranteed non-empty and `http(s)`-scheme).
 *
 * Use [displayTitle] when the surface needs a human label — it falls
 * back to the URL host when the title is empty.
 */
data class SafariPagePayload(
    val title: String,
    val url: String,
) {
    fun displayTitle(): String {
        if (title.isNotBlank()) return title
        return extractHost(url) ?: url
    }

    private fun extractHost(raw: String): String? {
        val afterScheme = raw.substringAfter("://", missingDelimiterValue = "")
        if (afterScheme.isEmpty()) return null
        val host = afterScheme.substringBefore('/').substringBefore('?').substringBefore('#')
        return host.takeIf { it.isNotEmpty() }
    }
}

/**
 * Pure validator for the page-context dictionary the iOS Safari Action
 * Extension hands the host app on invocation.
 *
 * Contract:
 *  - `rawUrl` must be non-null, non-blank, and start with `http://` or
 *    `https://` (case-insensitive). Anything else (javascript:,
 *    file:, ftp:, ...) is rejected with `null` — defensive against a
 *    misbehaving extension passing an unexpected scheme.
 *  - `rawTitle` is optional; null or blank collapses to `""` and the
 *    caller can use [SafariPagePayload.displayTitle] to fall back to
 *    the URL host.
 *  - Whitespace around both fields is trimmed.
 *  - The scheme check is the only URL validation here — full URL
 *    parsing and the http→https upgrade live in
 *    [com.po4yka.ratatoskr.util.url.SubmittedUrlNormalizer], which the
 *    caller composes after this atom.
 *
 * Pure, side-effect-free, deterministic.
 */
object SafariPagePayloadValidator {
    fun validate(
        rawTitle: String?,
        rawUrl: String?,
    ): SafariPagePayload? {
        val cleanedUrl = rawUrl?.trim().orEmpty()
        if (cleanedUrl.isEmpty()) return null
        val lower = cleanedUrl.lowercase()
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) return null

        val cleanedTitle = rawTitle?.trim().orEmpty()
        return SafariPagePayload(title = cleanedTitle, url = cleanedUrl)
    }
}
