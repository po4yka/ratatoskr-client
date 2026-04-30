package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Frost design system color tokens.
 *
 * Two-color rule: ink (text/foreground) + page (background), with a single critical accent: spark.
 * No shadows, no elevation. Alpha ladder defined in [FrostAlpha].
 */
@Immutable
data class FrostColors(
    val ink: Color,
    val page: Color,
    val spark: Color,
    val inkPure: Color,
    val pagePure: Color,
    val isDark: Boolean,
)

/** Frost light palette: ink-on-page editorial monospace. */
val frostLight =
    FrostColors(
        ink = Color(0xFF1C242C),
        page = Color(0xFFF0F2F5),
        spark = Color(0xFFDC3545),
        inkPure = Color(0xFF000000),
        pagePure = Color(0xFFFFFFFF),
        isDark = false,
    )

/** Frost dark palette: page-on-ink editorial monospace. spark stays constant. */
val frostDark =
    FrostColors(
        ink = Color(0xFFE8ECF0),
        page = Color(0xFF12161C),
        spark = Color(0xFFDC3545),
        inkPure = Color(0xFF000000),
        pagePure = Color(0xFFFFFFFF),
        isDark = true,
    )
