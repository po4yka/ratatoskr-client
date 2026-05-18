package com.po4yka.ratatoskr.util.observability

import kotlin.test.Test
import kotlin.test.assertEquals

class SentryConsentDecisionTest {
    @Test
    fun `consent granted + non-PII payload — Send`() {
        assertEquals(
            SentryConsentDecision.Outcome.Send,
            SentryConsentDecision.decide(
                consentGiven = true,
                hasPotentialPii = false,
            ),
        )
    }

    @Test
    fun `consent withdrawn — DropNoConsent regardless of PII`() {
        // Privacy gate is the strongest signal. Even a clean event
        // doesn't leave the device when the user opted out.
        assertEquals(
            SentryConsentDecision.Outcome.DropNoConsent,
            SentryConsentDecision.decide(
                consentGiven = false,
                hasPotentialPii = false,
            ),
        )
        assertEquals(
            SentryConsentDecision.Outcome.DropNoConsent,
            SentryConsentDecision.decide(
                consentGiven = false,
                hasPotentialPii = true,
            ),
        )
    }

    @Test
    fun `consent granted + suspected PII — DropPii`() {
        // The scrubber tried to clean the event but flagged that the
        // input matched a PII shape that didn't fully match a known
        // pattern. Conservative: drop rather than risk leaking.
        assertEquals(
            SentryConsentDecision.Outcome.DropPii,
            SentryConsentDecision.decide(
                consentGiven = true,
                hasPotentialPii = true,
            ),
        )
    }

    @Test
    fun `truth table is exhaustive`() {
        // Pin the four cells of the (consent x pii) matrix so a future
        // change can't accidentally introduce a fifth branch that
        // sends a Drop-classified event.
        val cells =
            mapOf(
                (true to false) to SentryConsentDecision.Outcome.Send,
                (true to true) to SentryConsentDecision.Outcome.DropPii,
                (false to false) to SentryConsentDecision.Outcome.DropNoConsent,
                (false to true) to SentryConsentDecision.Outcome.DropNoConsent,
            )
        cells.forEach { (input, expected) ->
            val (consent, pii) = input
            assertEquals(
                expected,
                SentryConsentDecision.decide(consentGiven = consent, hasPotentialPii = pii),
                "decide(consent=$consent, pii=$pii) should be $expected",
            )
        }
    }

    @Test
    fun `decision is deterministic`() {
        val a = SentryConsentDecision.decide(consentGiven = true, hasPotentialPii = false)
        val b = SentryConsentDecision.decide(consentGiven = true, hasPotentialPii = false)
        assertEquals(a, b)
    }
}
