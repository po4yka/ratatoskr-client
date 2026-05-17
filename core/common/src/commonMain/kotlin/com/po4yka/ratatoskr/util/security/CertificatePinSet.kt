package com.po4yka.ratatoskr.util.security

/**
 * Value type for a single SPKI SHA-256 certificate pin in the canonical
 * `sha256/<base64>` form used by OkHttp's `CertificatePinner` and the iOS
 * URLSessionDelegate trust-evaluation flow. Construction validates the
 * algorithm prefix and the base64 length so a misconfigured pin can't ship.
 */
data class CertificatePin private constructor(val sha256Base64: String) {
    val canonicalForm: String get() = "$PREFIX$sha256Base64"

    companion object {
        private const val PREFIX = "sha256/"
        private const val SHA256_BASE64_LENGTH = 44

        fun parse(raw: String): CertificatePin {
            require(raw.startsWith(PREFIX)) {
                "pin must use sha256 algorithm; got '${raw.substringBefore('/')}' in '$raw'"
            }
            val hash = raw.removePrefix(PREFIX)
            require(hash.length == SHA256_BASE64_LENGTH) {
                "sha256 base64 hash must be exactly $SHA256_BASE64_LENGTH chars; got ${hash.length}"
            }
            return CertificatePin(hash)
        }
    }
}

/**
 * The set of pins for a single host. Requires at least two pins (leaf +
 * backup intermediate) so a normal cert rotation can ship without bricking
 * installed builds — the spec is explicit about this rotation safety net.
 *
 * Host matching is exact: no wildcards, no port. Pinning is per-host by
 * design, and `shouldPin` answers the orthogonal question of whether the
 * currently-configured environment is the production environment at all
 * (so developers running against staging keep their flexible TLS).
 */
data class CertificatePinSet(val host: String, val pins: List<CertificatePin>) {
    init {
        require(pins.size >= MIN_PINS) {
            "certificate pin set for '$host' must contain at least $MIN_PINS pins " +
                "(leaf + backup intermediate); got ${pins.size}"
        }
    }

    fun appliesTo(host: String): Boolean = host == this.host

    companion object {
        const val MIN_PINS: Int = 2
        const val PRODUCTION_HOST: String = "api.ratatoskr.po4yka.com"

        fun shouldPin(baseUrl: String): Boolean = extractHost(baseUrl) == PRODUCTION_HOST

        private fun extractHost(baseUrl: String): String {
            val noScheme = baseUrl.substringAfter("://", missingDelimiterValue = baseUrl)
            return noScheme.substringBefore('/').substringBefore(':')
        }
    }
}
