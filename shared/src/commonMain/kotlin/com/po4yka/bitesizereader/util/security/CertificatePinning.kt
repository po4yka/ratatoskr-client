package com.po4yka.bitesizereader.util.security

/**
 * Certificate pinning configuration for the API client.
 *
 * Certificate pinning helps prevent man-in-the-middle attacks by validating
 * that the server's certificate matches a known certificate or public key.
 *
 * IMPLEMENTATION NOTES:
 * ---------------------
 * Certificate pinning requires platform-specific implementation:
 *
 * 1. ANDROID (OkHttp engine):
 *    Configure CertificatePinner in AndroidModule when creating the engine:
 *    ```kotlin
 *    val certificatePinner = CertificatePinner.Builder()
 *        .add(CertificatePinning.PINNED_HOSTNAME, CertificatePinning.PUBLIC_KEY_HASH_1)
 *        .add(CertificatePinning.PINNED_HOSTNAME, CertificatePinning.PUBLIC_KEY_HASH_2)
 *        .build()
 *
 *    val okHttpClient = OkHttpClient.Builder()
 *        .certificatePinner(certificatePinner)
 *        .build()
 *
 *    HttpClient(OkHttp) {
 *        engine {
 *            preconfigured = okHttpClient
 *        }
 *    }
 *    ```
 *
 * 2. iOS (Darwin engine):
 *    Implement custom URLSessionDelegate with certificate validation:
 *    ```swift
 *    func urlSession(_ session: URLSession,
 *                    didReceive challenge: URLAuthenticationChallenge,
 *                    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
 *        // Validate server certificate against pinned public key
 *    }
 *    ```
 *
 * 3. DESKTOP (OkHttp engine):
 *    Same as Android using OkHttp CertificatePinner.
 *
 * GETTING PIN HASHES:
 * -------------------
 * Use openssl to extract the public key hash:
 * ```bash
 * echo | openssl s_client -servername bitsizereaderapi.po4yka.com -connect bitsizereaderapi.po4yka.com:443 2>/dev/null | \
 *   openssl x509 -pubkey -noout | \
 *   openssl pkey -pubin -outform der | \
 *   openssl dgst -sha256 -binary | \
 *   openssl enc -base64
 * ```
 *
 * PIN ROTATION:
 * -------------
 * Always pin at least 2 certificates (current + backup) to allow for certificate rotation
 * without breaking the app. Update pins before certificate expiry.
 */
object CertificatePinning {
    /**
     * Hostname to pin certificates for.
     * This should match the API base URL hostname.
     */
    const val PINNED_HOSTNAME = "bitsizereaderapi.po4yka.com"

    /**
     * SHA-256 public key hash for the primary certificate.
     * Format: "sha256/BASE64_ENCODED_HASH"
     *
     * IMPORTANT: Replace with actual hash from production certificate.
     * Use the openssl command documented above to obtain this value.
     */
    const val PUBLIC_KEY_HASH_1 = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="

    /**
     * SHA-256 public key hash for the backup certificate.
     * This should be a different certificate (e.g., next rotation, different CA).
     *
     * IMPORTANT: Replace with actual hash from backup certificate.
     */
    const val PUBLIC_KEY_HASH_2 = "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="

    /**
     * Whether certificate pinning is enabled.
     * Set to false in development/debug builds if needed.
     * Should ALWAYS be true in production.
     */
    var enabled: Boolean = false // TODO: Enable after adding real certificate hashes

    /**
     * All pinned public key hashes for the API hostname.
     */
    val pinnedHashes: List<String>
        get() = if (enabled) listOf(PUBLIC_KEY_HASH_1, PUBLIC_KEY_HASH_2) else emptyList()
}
