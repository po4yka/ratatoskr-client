package com.po4yka.ratatoskr.util.observability

/**
 * Pure decision atom for whether to show the diagnostics surfaces
 * (Settings → Help → Sync health screen, Frost Lab component browser,
 * debug log dump, etc.).
 *
 * Rule:
 *  - Debug builds always show diagnostics regardless of the toggle.
 *  - Release builds show diagnostics only when the user has flipped
 *    `Settings → Help → Diagnostics → Enable diagnostics` to on.
 *
 * In boolean terms: `!isReleaseBuild || diagnosticsToggleEnabled`.
 *
 * Pure, side-effect-free, deterministic.
 */
object DiagnosticsVisibility {
    fun shouldShow(
        isReleaseBuild: Boolean,
        diagnosticsToggleEnabled: Boolean,
    ): Boolean = !isReleaseBuild || diagnosticsToggleEnabled
}
