package com.po4yka.ratatoskr.util.deeplink

/**
 * Parses incoming intent / NSUserActivity URIs into a sealed [RatatoskrDeepLink]
 * hierarchy. Two surfaces are supported:
 *
 *  - The verified `https://ratatoskr.po4yka.com/s/{id}` Universal / App Link
 *    (App Link verification only matches HTTPS on the canonical host).
 *  - The `ratatoskr://` custom scheme retained as a widget fallback:
 *    `ratatoskr://summary/{id}` and `ratatoskr://submit-url?url={percent-encoded}`.
 *
 * Strict path shape: any extra segment after the id falls through to [Unknown]
 * so a misclick on `/s/{id}/highlight` doesn't silently open the summary.
 * Wrong hosts and plain-HTTP variants are rejected for phishing defense and
 * App Link consistency. Inputs are normalized to lowercase before scheme/host
 * comparison because OS payloads arrive with mixed casing.
 *
 * Percent-decoding is done by hand on the `submit-url` `url` parameter — Kotlin
 * commonMain has no URLDecoder, and the only thing we need to round-trip is
 * the URL the share-extension percent-encoded for us. ASCII-only by design;
 * URLs that need byte-level UTF-8 decoding are out of scope for this parser
 * because the source (browser share extension, our own widget) always emits
 * ASCII-encoded URLs.
 */
sealed interface RatatoskrDeepLink {
    data class OpenSummary(val id: String) : RatatoskrDeepLink

    data class SubmitUrl(val url: String) : RatatoskrDeepLink

    data class Unknown(val raw: String) : RatatoskrDeepLink
}

object RatatoskrDeepLinkParser {
    const val WEB_HOST: String = "ratatoskr.po4yka.com"
    const val APP_SCHEME: String = "ratatoskr"
    private const val WEB_SUMMARY_SEGMENT = "s"
    private const val APP_SUMMARY_HOST = "summary"
    private const val APP_SUBMIT_HOST = "submit-url"
    private const val SUBMIT_URL_PARAM = "url"

    fun parse(raw: String): RatatoskrDeepLink {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return RatatoskrDeepLink.Unknown(raw)

        val schemeEnd = trimmed.indexOf("://")
        if (schemeEnd <= 0) return RatatoskrDeepLink.Unknown(raw)

        val scheme = trimmed.substring(0, schemeEnd).lowercase()
        val authorityAndPath = trimmed.substring(schemeEnd + 3)
        val (beforeQuery, query) = splitOnce(authorityAndPath, '?')
        val parts = beforeQuery.split('/')
        val host = parts.firstOrNull()?.lowercase().orEmpty()
        val pathSegments = parts.drop(1).filter { it.isNotEmpty() }

        return when (scheme) {
            "https" -> parseHttps(host, pathSegments, raw)
            APP_SCHEME -> parseAppScheme(host, pathSegments, query, raw)
            else -> RatatoskrDeepLink.Unknown(raw)
        }
    }

    private fun parseHttps(
        host: String,
        pathSegments: List<String>,
        raw: String,
    ): RatatoskrDeepLink {
        if (host != WEB_HOST) return RatatoskrDeepLink.Unknown(raw)
        if (pathSegments.size != 2) return RatatoskrDeepLink.Unknown(raw)
        if (pathSegments[0] != WEB_SUMMARY_SEGMENT) return RatatoskrDeepLink.Unknown(raw)
        val id = pathSegments[1]
        if (id.isEmpty()) return RatatoskrDeepLink.Unknown(raw)
        return RatatoskrDeepLink.OpenSummary(id)
    }

    private fun parseAppScheme(
        host: String,
        pathSegments: List<String>,
        query: String,
        raw: String,
    ): RatatoskrDeepLink =
        when (host) {
            APP_SUMMARY_HOST -> {
                if (pathSegments.size == 1 && pathSegments[0].isNotEmpty()) {
                    RatatoskrDeepLink.OpenSummary(pathSegments[0])
                } else {
                    RatatoskrDeepLink.Unknown(raw)
                }
            }
            APP_SUBMIT_HOST -> {
                val urlEncoded = queryParam(query, SUBMIT_URL_PARAM)
                if (urlEncoded.isNullOrEmpty()) {
                    RatatoskrDeepLink.Unknown(raw)
                } else {
                    RatatoskrDeepLink.SubmitUrl(percentDecode(urlEncoded))
                }
            }
            else -> RatatoskrDeepLink.Unknown(raw)
        }

    private fun splitOnce(
        source: String,
        delimiter: Char,
    ): Pair<String, String> {
        val idx = source.indexOf(delimiter)
        return if (idx < 0) source to "" else source.substring(0, idx) to source.substring(idx + 1)
    }

    private fun queryParam(
        query: String,
        name: String,
    ): String? {
        if (query.isEmpty()) return null
        return query.split('&').asSequence()
            .map { splitOnce(it, '=') }
            .firstOrNull { it.first == name }
            ?.second
    }

    private fun percentDecode(input: String): String {
        if ('%' !in input && '+' !in input) return input
        val sb = StringBuilder(input.length)
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c == '+' -> {
                    sb.append(' ')
                    i++
                }
                c == '%' && i + 2 < input.length -> {
                    val code = input.substring(i + 1, i + 3).toIntOrNull(16)
                    if (code != null) {
                        sb.append(code.toChar())
                        i += 3
                    } else {
                        sb.append(c)
                        i++
                    }
                }
                else -> {
                    sb.append(c)
                    i++
                }
            }
        }
        return sb.toString()
    }
}
