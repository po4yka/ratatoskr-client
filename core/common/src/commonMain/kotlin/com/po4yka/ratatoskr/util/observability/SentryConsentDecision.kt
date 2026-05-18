package com.po4yka.ratatoskr.util.observability

/**
 * Pure pre-send gate for Sentry events. Composes two orthogonal signals
 * into a single outcome the `KermitSentryWriter` can act on:
 *  - `consentGiven`     — the `UserPreferences.crashReportingEnabled`
 *                          toggle.
 *  - `hasPotentialPii`  — a flag set by the caller after running the
 *                          payload through `SentryEventScrubber` and
 *                          finding text that matched a PII-like shape
 *                          but couldn't be cleanly redacted.
 *
 * Outcomes:
 *  - [Outcome.Send]            — both gates open. Forward to Sentry.
 *  - [Outcome.DropNoConsent]   — consent gate closed. Wins over the
 *                                  PII signal so an opted-out user
 *                                  never has any event sent.
 *  - [Outcome.DropPii]         — consent open but PII flagged. Drop
 *                                  rather than risk leaking; the
 *                                  caller can log a counter so we
 *                                  notice the scrubber is missing a
 *                                  pattern.
 *
 * Pure, side-effect-free, deterministic.
 */
object SentryConsentDecision {
    enum class Outcome {
        Send,
        DropNoConsent,
        DropPii,
    }

    fun decide(
        consentGiven: Boolean,
        hasPotentialPii: Boolean,
    ): Outcome {
        if (!consentGiven) return Outcome.DropNoConsent
        if (hasPotentialPii) return Outcome.DropPii
        return Outcome.Send
    }
}
