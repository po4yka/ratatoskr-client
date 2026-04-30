@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.jetbrains_mono_extra_bold
import ratatoskr.core.ui.generated.resources.jetbrains_mono_medium
import ratatoskr.core.ui.generated.resources.jetbrains_mono_regular

/**
 * Project-owned type scale. Carbon-shaped slot names retained for migration ergonomics.
 * Values now use JetBrains Mono (Frost editorial monospace). Slots will be deleted in the
 * final migration commit — new code should use AppTheme.type Frost slots instead.
 *
 * Construct via [rememberDefaultAppType] — font loading requires Composable context.
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

/**
 * Builds and remembers the default [AppType] for the current composition.
 * Called by [RatatoskrTheme]; call sites outside the theme should use [AppTheme.type].
 */
@Composable
fun rememberDefaultAppType(): AppType {
    val regular = Font(Res.font.jetbrains_mono_regular, FontWeight.Normal)
    val medium = Font(Res.font.jetbrains_mono_medium, FontWeight.Medium)
    val extraBold = Font(Res.font.jetbrains_mono_extra_bold, FontWeight.ExtraBold)
    val monoFamily =
        remember(regular, medium, extraBold) {
            FontFamily(regular, medium, extraBold)
        }
    return remember(monoFamily) {
        AppType(
            body01 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = (13 * 1.30).sp,
                    letterSpacing = 0.3.sp,
                ),
            bodyCompact01 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = (13 * 1.30).sp,
                    letterSpacing = 0.3.sp,
                ),
            heading02 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    lineHeight = (13 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
            heading03 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = (16 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
            heading04 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    lineHeight = (22 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
            headingCompact01 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    lineHeight = (13 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
            label01 =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = (11 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
        )
    }
}
