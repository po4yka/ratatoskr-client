package com.po4yka.ratatoskr.util.observability

/**
 * Pure text-level PII scrubber for crash-reporter payloads. Sentry events,
 * Kermit log lines that get forwarded as breadcrumbs, and analytics
 * exception messages all carry user-visible text that may include personal
 * data: emails (server validation errors echo the request body), bearer
 * tokens (Ktor exception messages quote the full Authorization header),
 * URL credentials in the `https://user:pass@host` shape, and query-string
 * tokens (`?token=abc`).
 *
 * The order of redactions matters:
 *  1. URL userinfo (`//user:pass@host`) — strip before email so the
 *     password fragment doesn't survive as part of an email match.
 *  2. Bearer tokens — preserve the scheme word case so the engineer
 *     reading Sentry recognizes the auth-header context.
 *  3. Query-string tokens — preserve the key (helps triage) but strip
 *     the value.
 *  4. Emails — anything that survived the prior passes.
 *
 * Bearer false-positive defense: the regex requires `>= 16` token chars
 * so "Bearer of bad news" is not mistaken for a header. The token char
 * class is the JWT/base64 superset (`A-Za-z0-9._~+/=-`).
 *
 * Email false-positive defense: the pattern requires a `.<TLD>` suffix
 * so `@channel` mentions, `asset@2x` image qualifiers, and `user@local`
 * dev addresses are not redacted as PII.
 *
 * Query-string token allowlist is conservative: `token`, `access_token`,
 * `api_key`, `password`, `refresh_token`, `secret`. The bare `key`
 * parameter is intentionally excluded because it's overloaded in non-auth
 * contexts (cache keys, sort keys, identity column names).
 */
object SentryEventScrubber {
    const val EMAIL_REDACTION: String = "[EMAIL]"
    const val TOKEN_REDACTION: String = "[REDACTED]"
    const val URL_CREDENTIAL_REDACTION: String = "[REDACTED]"

    private val URL_USERINFO_PATTERN = Regex("//[^:/@\\s]+:[^@\\s]+@")
    private val BEARER_PATTERN =
        Regex("(Bearer)\\s+[A-Za-z0-9._~+/=\\-]{16,}", RegexOption.IGNORE_CASE)
    private val QUERY_TOKEN_PATTERN =
        Regex(
            "([?&](?:token|access_token|api_key|password|refresh_token|secret)=)[^&\\s]+",
            RegexOption.IGNORE_CASE,
        )
    private val EMAIL_PATTERN =
        Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")

    fun scrub(text: String): String {
        if (text.isBlank()) return text
        var result = text
        result = URL_USERINFO_PATTERN.replace(result) { "//$URL_CREDENTIAL_REDACTION@" }
        result = BEARER_PATTERN.replace(result) { "${it.groupValues[1]} $TOKEN_REDACTION" }
        result = QUERY_TOKEN_PATTERN.replace(result) { "${it.groupValues[1]}$TOKEN_REDACTION" }
        result = EMAIL_PATTERN.replace(result) { EMAIL_REDACTION }
        return result
    }
}
