@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Project-owned color tokens. Field set mirrors the tokens referenced from the codebase before
 * the previous design system was removed. Seed values were captured one-time from the previous
 * light + dark themes; the project owns these values henceforth.
 *
 * The token names are kept as-is to keep the removal codemod purely mechanical. Future
 * design-system work can rename or recolor these in place.
 */
data class AppColors(
    val background: Color,
    val backgroundInverse: Color,
    val borderSubtle00: Color,
    val borderDisabled: Color,
    val borderInteractive: Color,
    val interactive: Color,
    val linkPrimary: Color,
    val supportError: Color,
    val supportErrorInverse: Color,
    val supportSuccess: Color,
    val supportWarning: Color,
    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconDisabled: Color,
    val layer01: Color,
    val layer02: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnColor: Color,
    val textOnColorDisabled: Color,
    val textDisabled: Color,
    val textPlaceholder: Color,
)

/** Light token set, seeded from the previous design system's light theme. */
val lightAppColors: AppColors =
    AppColors(
        background = Color(0xFFFFFFFF),
        backgroundInverse = Color(0xFF393939),
        borderSubtle00 = Color(0xFFE0E0E0),
        borderDisabled = Color(0xFFC6C6C6),
        borderInteractive = Color(0xFF0F62FE),
        interactive = Color(0xFF0F62FE),
        linkPrimary = Color(0xFF0F62FE),
        supportError = Color(0xFFDA1E28),
        supportErrorInverse = Color(0xFFFA4D56),
        supportSuccess = Color(0xFF24A148),
        supportWarning = Color(0xFFF1C21B),
        iconPrimary = Color(0xFF161616),
        iconSecondary = Color(0xFF525252),
        iconDisabled = Color(0x3F161616),
        layer01 = Color(0xFFF4F4F4),
        layer02 = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF161616),
        textSecondary = Color(0xFF525252),
        textOnColor = Color(0xFFFFFFFF),
        textOnColorDisabled = Color(0xFF8D8D8D),
        textDisabled = Color(0x3F161616),
        textPlaceholder = Color(0x66161616),
    )

/** Dark token set, seeded from the previous design system's dark theme. */
val darkAppColors: AppColors =
    AppColors(
        background = Color(0xFF161616),
        backgroundInverse = Color(0xFFF4F4F4),
        borderSubtle00 = Color(0xFF393939),
        borderDisabled = Color(0x7F8D8D8D),
        borderInteractive = Color(0xFF4589FF),
        interactive = Color(0xFF4589FF),
        linkPrimary = Color(0xFF78A9FF),
        supportError = Color(0xFFFA4D56),
        supportErrorInverse = Color(0xFFDA1E28),
        supportSuccess = Color(0xFF42BE65),
        supportWarning = Color(0xFFF1C21B),
        iconPrimary = Color(0xFFF4F4F4),
        iconSecondary = Color(0xFFC6C6C6),
        iconDisabled = Color(0x3FF4F4F4),
        layer01 = Color(0xFF262626),
        layer02 = Color(0xFF393939),
        textPrimary = Color(0xFFF4F4F4),
        textSecondary = Color(0xFFC6C6C6),
        textOnColor = Color(0xFFFFFFFF),
        textOnColorDisabled = Color(0x3FFFFFFF),
        textDisabled = Color(0x3FF4F4F4),
        textPlaceholder = Color(0x66F4F4F4),
    )
