package com.po4yka.ratatoskr.util.url

/**
 * Trims a URL down to a glyph-budgeted display string for the clipboard
 * suggestion banner and the share-target ranker. The banner's
 * `BracketButton` label has a tight width; rendering a raw 120-char URL
 * pushes the bracket caps off-screen on narrow phones.
 *
 * Output shape, by priority:
 *  - protocol prefix (`http://`, `https://`) and a literal `www.` subdomain
 *    are stripped first — neither is meaningful for the user at this scale.
 *  - the trailing slash is removed.
 *  - if the resulting string fits the budget, it is returned as-is.
 *  - otherwise the trimmer tries `host/…/last-path-segment` so the user
 *    can still see which page they are about to submit.
 *  - if even that overflows, it falls back to the bare host.
 *  - if the host alone overflows the budget, it is hard-truncated to
 *    `head…` reserving one glyph for the ellipsis.
 *
 * Pure, side-effect-free, idempotent — call twice and you get the same
 * string back. The trimmer never throws; null / blank / non-positive
 * budgets collapse to the empty string.
 */
object DisplayUrlTrimmer {
    private const val ELLIPSIS = "…"
    private const val WWW_PREFIX = "www."
    private const val HTTPS_PREFIX = "https://"
    private const val HTTP_PREFIX = "http://"

    fun trim(
        url: String?,
        maxLength: Int,
    ): String {
        if (maxLength <= 0) return ""
        val raw = url?.trim().orEmpty()
        if (raw.isEmpty()) return ""

        val stripped = stripChrome(raw)
        if (stripped.length <= maxLength) return stripped

        val slash = stripped.indexOf('/')
        val host = if (slash >= 0) stripped.substring(0, slash) else stripped
        val lastSegment = if (slash >= 0) stripped.substringAfterLast('/') else null
        if (lastSegment != null && lastSegment.isNotEmpty() && lastSegment != host) {
            val candidate = host + "/" + ELLIPSIS + "/" + lastSegment
            if (candidate.length <= maxLength) return candidate
        }
        if (host.length <= maxLength) return host
        return host.take(maxLength - 1) + ELLIPSIS
    }

    private fun stripChrome(raw: String): String {
        var s =
            when {
                raw.startsWith(HTTPS_PREFIX) -> raw.substring(HTTPS_PREFIX.length)
                raw.startsWith(HTTP_PREFIX) -> raw.substring(HTTP_PREFIX.length)
                else -> raw
            }
        if (s.startsWith(WWW_PREFIX)) s = s.substring(WWW_PREFIX.length)
        return s.trimEnd('/')
    }
}
