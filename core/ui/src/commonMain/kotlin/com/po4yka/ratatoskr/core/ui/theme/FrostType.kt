@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.jetbrains_mono_extra_bold
import ratatoskr.core.ui.generated.resources.jetbrains_mono_medium
import ratatoskr.core.ui.generated.resources.jetbrains_mono_regular
import ratatoskr.core.ui.generated.resources.source_serif_4_medium_italic

/**
 * Frost type scale using JetBrains Mono (editorial monospace) and Source Serif 4 (reader serif).
 *
 * Construct via [rememberFrostType] inside a Composable — font loading requires composition.
 */
@Immutable
data class FrostType(
    val monoXs: TextStyle,
    val monoSm: TextStyle,
    val monoBody: TextStyle,
    val monoEmph: TextStyle,
    val serifReader: TextStyle,
    val serifReaderZoom: TextStyle,
)

/**
 * Builds and remembers [FrostType] for the current composition.
 * Call once near the root (e.g. in [RatatoskrTheme]).
 */
@Composable
fun rememberFrostType(): FrostType {
    val regular = Font(Res.font.jetbrains_mono_regular, FontWeight.Normal)
    val medium = Font(Res.font.jetbrains_mono_medium, FontWeight.Medium)
    val extraBold = Font(Res.font.jetbrains_mono_extra_bold, FontWeight.ExtraBold)
    val serifIt = Font(Res.font.source_serif_4_medium_italic, FontWeight.Medium, FontStyle.Italic)
    val monoFamily =
        remember(regular, medium, extraBold) {
            FontFamily(regular, medium, extraBold)
        }
    val serifFamily =
        remember(serifIt) {
            FontFamily(serifIt)
        }
    return remember(monoFamily, serifFamily) {
        FrostType(
            monoXs =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = (11 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
            monoSm =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = (12 * 1.30).sp,
                    letterSpacing = 0.3.sp,
                ),
            monoBody =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = (13 * 1.30).sp,
                    letterSpacing = 0.3.sp,
                ),
            monoEmph =
                TextStyle(
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    lineHeight = (13 * 1.30).sp,
                    letterSpacing = 1.sp,
                ),
            serifReader =
                TextStyle(
                    fontFamily = serifFamily,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    lineHeight = (16 * 1.55).sp,
                    letterSpacing = 0.sp,
                ),
            serifReaderZoom =
                TextStyle(
                    fontFamily = serifFamily,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    fontSize = 22.sp,
                    lineHeight = (22 * 1.55).sp,
                    letterSpacing = 0.sp,
                ),
        )
    }
}
