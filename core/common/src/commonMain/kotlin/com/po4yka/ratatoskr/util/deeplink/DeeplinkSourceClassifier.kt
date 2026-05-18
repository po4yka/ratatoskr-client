package com.po4yka.ratatoskr.util.deeplink

/**
 * Provenance tag for a deep-link URI — where the OS handed it to us.
 * Useful for analytics that need to distinguish a Universal/App-Link
 * tap (organic web share) from a custom-scheme tap (widget /
 * notification PendingIntent) without re-parsing the URI.
 */
enum class DeeplinkSource {
    UniversalLink,
    CustomScheme,
    Unknown,
}

/**
 * Pure classifier that maps a raw deep-link URI string to its
 * [DeeplinkSource]. Does **not** parse the URI further — that is the
 * job of [RatatoskrDeepLinkParser]. The classifier exists so analytics
 * and routing code can tag the entry path with a single string check.
 *
 * Rules:
 *  - `http(s)://ratatoskr.po4yka.com/...` → [DeeplinkSource.UniversalLink]
 *  - `ratatoskr://...`                     → [DeeplinkSource.CustomScheme]
 *  - everything else (including null / blank / garbage / other hosts /
 *    other schemes) → [DeeplinkSource.Unknown]
 *
 * Scheme and host comparisons are case-insensitive. Defensive against
 * null and blank input — never throws.
 *
 * Pure, side-effect-free, deterministic.
 */
object DeeplinkSourceClassifier {
    private const val CANONICAL_HOST: String = "ratatoskr.po4yka.com"
    private const val CUSTOM_SCHEME_PREFIX: String = "ratatoskr://"
    private const val HTTPS_PREFIX: String = "https://"
    private const val HTTP_PREFIX: String = "http://"

    fun classify(raw: String?): DeeplinkSource {
        if (raw.isNullOrBlank()) return DeeplinkSource.Unknown
        val lower = raw.trim().lowercase()

        if (lower.startsWith(CUSTOM_SCHEME_PREFIX)) {
            return DeeplinkSource.CustomScheme
        }
        if (lower.startsWith(HTTPS_PREFIX) || lower.startsWith(HTTP_PREFIX)) {
            val afterScheme = lower.substringAfter("://", missingDelimiterValue = "")
            val host = afterScheme.substringBefore('/').substringBefore('?').substringBefore('#')
            if (host == CANONICAL_HOST) {
                return DeeplinkSource.UniversalLink
            }
        }
        return DeeplinkSource.Unknown
    }
}
