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

/**
 * Frost sepia reading palette: warm dark ink on cream page for low-light long-form reading.
 * Strictly two-color per Frost rules — no rainbow accents. Spark stays #DC3545.
 */
val frostSepia =
    FrostColors(
        ink = Color(0xFF433324),
        page = Color(0xFFF4EFE0),
        spark = Color(0xFFDC3545),
        inkPure = Color(0xFF000000),
        pagePure = Color(0xFFFFFFFF),
        isDark = false,
    )

/**
 * Frost high-contrast reading palette: pure black on pure white. Use when the user has set
 * the OS high-contrast accessibility flag or explicitly opted in via reading preferences.
 */
val frostHighContrast =
    FrostColors(
        ink = Color(0xFF000000),
        page = Color(0xFFFFFFFF),
        spark = Color(0xFFDC3545),
        inkPure = Color(0xFF000000),
        pagePure = Color(0xFFFFFFFF),
        isDark = false,
    )

/**
 * Reading-time palette selector. Only applies inside the reading detail surface — the rest of
 * the app stays in Frost MONO. Resolves through [paletteFor].
 */
enum class ReadingTheme { MONO_LIGHT, MONO_DARK, SEPIA, HIGH_CONTRAST }

/**
 * Resolves a [ReadingTheme] to a concrete [FrostColors] palette. `MONO_*` inherits the
 * project default light/dark; `SEPIA` and `HIGH_CONTRAST` switch to their dedicated palettes
 * regardless of system dark-mode.
 */
fun paletteFor(theme: ReadingTheme): FrostColors =
    when (theme) {
        ReadingTheme.MONO_LIGHT -> frostLight
        ReadingTheme.MONO_DARK -> frostDark
        ReadingTheme.SEPIA -> frostSepia
        ReadingTheme.HIGH_CONTRAST -> frostHighContrast
    }
