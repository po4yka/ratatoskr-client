@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Carbon-shaped slot names retained for migration ergonomics; values now map to Frost
 * ink/page/spark + alpha ladder. Slots will be deleted in the final migration commit.
 *
 * Light:  ink=#1C242C  page=#F0F2F5  spark=#DC3545
 * Dark:   ink=#E8ECF0  page=#12161C  spark=#DC3545 (constant)
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

/** Light token set — reseeded to Frost ink/page/spark values. */
val lightAppColors: AppColors =
    run {
        val ink = Color(0xFF1C242C)
        val page = Color(0xFFF0F2F5)
        val spark = Color(0xFFDC3545)
        AppColors(
            background = page,
            backgroundInverse = ink,
            borderSubtle00 = ink.copy(alpha = 0.40f),
            borderDisabled = ink.copy(alpha = 0.25f),
            borderInteractive = ink,
            interactive = ink,
            linkPrimary = ink,
            supportError = spark,
            supportErrorInverse = spark,
            supportSuccess = ink,
            supportWarning = ink,
            iconPrimary = ink,
            iconSecondary = ink.copy(alpha = 0.70f),
            iconDisabled = ink.copy(alpha = 0.25f),
            layer01 = page,
            layer02 = page,
            textPrimary = ink,
            textSecondary = ink.copy(alpha = 0.70f),
            textOnColor = page,
            textOnColorDisabled = page.copy(alpha = 0.50f),
            textDisabled = ink.copy(alpha = 0.25f),
            textPlaceholder = ink.copy(alpha = 0.50f),
        )
    }

/** Dark token set — reseeded to Frost ink/page/spark values. */
val darkAppColors: AppColors =
    run {
        val ink = Color(0xFFE8ECF0)
        val page = Color(0xFF12161C)
        val spark = Color(0xFFDC3545)
        AppColors(
            background = page,
            backgroundInverse = ink,
            borderSubtle00 = ink.copy(alpha = 0.40f),
            borderDisabled = ink.copy(alpha = 0.25f),
            borderInteractive = ink,
            interactive = ink,
            linkPrimary = ink,
            supportError = spark,
            supportErrorInverse = spark,
            supportSuccess = ink,
            supportWarning = ink,
            iconPrimary = ink,
            iconSecondary = ink.copy(alpha = 0.70f),
            iconDisabled = ink.copy(alpha = 0.25f),
            layer01 = page,
            layer02 = page,
            textPrimary = ink,
            textSecondary = ink.copy(alpha = 0.70f),
            textOnColor = page,
            textOnColorDisabled = page.copy(alpha = 0.50f),
            textDisabled = ink.copy(alpha = 0.25f),
            textPlaceholder = ink.copy(alpha = 0.50f),
        )
    }
