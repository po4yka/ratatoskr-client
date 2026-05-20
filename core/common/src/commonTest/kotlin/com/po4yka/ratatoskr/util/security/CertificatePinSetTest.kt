package com.po4yka.ratatoskr.util.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CertificatePinSetTest {
    // Two valid SHA-256-of-SPKI base64 strings (44 chars each, ending with "=").
    // Real pin material is host- and key-specific; these are synthetic for tests.
    private val leafPin = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    private val backupPin = "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
    private val productionHost = "ratatoskr-api.po4yka.com"

    @Test
    fun `valid sha256 pin parses and stores the hash`() {
        val pin = CertificatePin.parse(leafPin)
        assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", pin.sha256Base64)
    }

    @Test
    fun `pin must use sha256 algorithm — sha1 and others are rejected`() {
        // Regression guard: SHA-1 has been deprecated for cryptographic use for a
        // decade. Accepting a sha1/ pin silently would defeat the entire MASVS
        // NETWORK-1 control even though the call site looks correctly wired.
        assertFailsWith<IllegalArgumentException> {
            CertificatePin.parse("sha1/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        }
        assertFailsWith<IllegalArgumentException> {
            CertificatePin.parse("md5/AAAAAAAAAAAAAAAAAAAAAAAA")
        }
    }

    @Test
    fun `pin without algorithm prefix is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            CertificatePin.parse("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        }
    }

    @Test
    fun `pin with wrong base64 length is rejected`() {
        // A SHA-256 digest is 32 bytes → 44 chars of base64 with one "=" pad.
        // Anything else is malformed and would silently fail TLS handshake
        // negotiation in confusing ways at runtime.
        assertFailsWith<IllegalArgumentException> {
            CertificatePin.parse("sha256/AAAA")
        }
    }

    @Test
    fun `pin set requires at least two pins — leaf and backup intermediate`() {
        // Spec: "Two pins (leaf + backup intermediate) to allow rotation without
        // bricking installed builds." A single-pin set is a self-inflicted DoS
        // waiting for the next cert rotation. The constructor enforces this so
        // a misconfigured AppConfig can't ship.
        assertFailsWith<IllegalArgumentException> {
            CertificatePinSet(
                host = productionHost,
                pins = listOf(CertificatePin.parse(leafPin)),
            )
        }
    }

    @Test
    fun `pin set with leaf and backup pins constructs successfully`() {
        val set =
            CertificatePinSet(
                host = productionHost,
                pins = listOf(CertificatePin.parse(leafPin), CertificatePin.parse(backupPin)),
            )

        assertEquals(productionHost, set.host)
        assertEquals(2, set.pins.size)
    }

    @Test
    fun `appliesTo matches the configured host exactly — no wildcard, no port`() {
        // Wildcard host matching is a footgun: an attacker who can MITM
        // x.ratatoskr-api.po4yka.com should not benefit from the api pin set.
        // Pinning is per-host by design.
        val set =
            CertificatePinSet(
                host = productionHost,
                pins = listOf(CertificatePin.parse(leafPin), CertificatePin.parse(backupPin)),
            )

        assertTrue(set.appliesTo(productionHost))
        assertFalse(set.appliesTo("other.example.com"))
        assertFalse(set.appliesTo("sub.ratatoskr-api.po4yka.com"))
    }

    @Test
    fun `non-production base URLs skip pinning — local and staging keep their flexible TLS`() {
        // Spec: "No pinning for non-prod environments configured by api.base.url
        // override." This protects developers running against staging or a local
        // backend with self-signed certs.
        assertFalse(CertificatePinSet.shouldPin("https://localhost:8080"))
        assertFalse(CertificatePinSet.shouldPin("https://staging.ratatoskr.po4yka.com"))
        assertFalse(CertificatePinSet.shouldPin("https://api.dev.example.com"))
    }

    @Test
    fun `production base URL opts into pinning`() {
        // Boundary: the production hostname is exactly ratatoskr-api.po4yka.com.
        // Anything else is treated as non-prod.
        assertTrue(CertificatePinSet.shouldPin("https://ratatoskr-api.po4yka.com"))
        assertTrue(CertificatePinSet.shouldPin("https://ratatoskr-api.po4yka.com/v1/auth/refresh"))
    }

    @Test
    fun `host extraction tolerates missing scheme and trailing path`() {
        // Defensive: AppConfig.Api.baseUrl is parsed elsewhere and could arrive
        // with or without a scheme depending on the caller. The helper must
        // normalize without crashing.
        assertTrue(CertificatePinSet.shouldPin("ratatoskr-api.po4yka.com"))
        assertTrue(CertificatePinSet.shouldPin("ratatoskr-api.po4yka.com/health"))
    }
}
