@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Project-owned type scale. Field set used by AppTheme.type.X across the codebase.
 *
 * Future design-system work can replace these values without touching call sites.
 */
data class AppType(
    val body01: TextStyle,
    val bodyCompact01: TextStyle,
    val heading02: TextStyle,
    val heading03: TextStyle,
    val heading04: TextStyle,
    val headingCompact01: TextStyle,
    val label01: TextStyle,
)

/** Default type scale. */
val defaultAppType: AppType =
    AppType(
        body01 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.16.sp,
            ),
        bodyCompact01 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.16.sp,
            ),
        heading02 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        heading03 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        heading04 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
        headingCompact01 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.16.sp,
            ),
        label01 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.32.sp,
            ),
    )
