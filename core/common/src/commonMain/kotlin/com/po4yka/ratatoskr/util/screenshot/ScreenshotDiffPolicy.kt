package com.po4yka.ratatoskr.util.screenshot

/**
 * Whether the screenshot run is *recording* fresh goldens or *verifying*
 * the current render against existing goldens. Roborazzi exposes the
 * same dichotomy; this enum lets the decision atom stay platform-free.
 */
enum class ScreenshotMode {
    Verify,
    Record,
}

/** Outcome of one screenshot comparison. */
sealed interface ScreenshotOutcome {
    data object Pass : ScreenshotOutcome

    data class Fail(val reason: String) : ScreenshotOutcome

    data object UpdateGolden : ScreenshotOutcome
}

/**
 * Pure verdict atom for the upcoming Roborazzi-driven Frost snapshot
 * tests. Compares a measured pixel-diff ratio against a per-atom
 * threshold and produces a [ScreenshotOutcome] the test runner can act
 * on:
 *  - [ScreenshotMode.Record]            → [ScreenshotOutcome.UpdateGolden]
 *  - Verify + `diffRatio` is NaN        → [ScreenshotOutcome.Fail]
 *  - Verify + `diffRatio <= threshold`  → [ScreenshotOutcome.Pass]
 *  - Verify + `diffRatio  > threshold`  → [ScreenshotOutcome.Fail]
 *
 * Threshold comparison is inclusive (≤): a per-atom threshold of 0.005
 * accepts exactly 0.5% drift. Negative diff inputs are coerced to zero
 * so noisy diff engines that report -0.0 for identical images behave
 * predictably. NaN routes to Fail — a NaN diff means the engine itself
 * failed and the test should surface the problem.
 *
 * Pure, side-effect-free, deterministic.
 */
object ScreenshotDiffPolicy {
    fun decide(
        diffRatio: Double,
        threshold: Double,
        mode: ScreenshotMode,
    ): ScreenshotOutcome {
        if (mode == ScreenshotMode.Record) return ScreenshotOutcome.UpdateGolden
        if (diffRatio.isNaN()) {
            return ScreenshotOutcome.Fail(reason = "diff engine reported NaN")
        }
        val effective = if (diffRatio < 0.0) 0.0 else diffRatio
        return if (effective <= threshold) {
            ScreenshotOutcome.Pass
        } else {
            ScreenshotOutcome.Fail(reason = "diffRatio $effective exceeded threshold $threshold")
        }
    }
}
