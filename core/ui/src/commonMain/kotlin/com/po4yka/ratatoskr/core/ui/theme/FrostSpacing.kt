package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Frost spacing scale based on an 8dp cell grid.
 *
 * Named slots map to layout roles rather than T-shirt sizes.
 * Prefer [AppTheme.spacing] over [Spacing] in new code.
 */
@Immutable
data class FrostSpacing(
    val cell: Dp = 8.dp,
    val halfLine: Dp = 8.dp,
    val line: Dp = 16.dp,
    val gapExt: Dp = 10.dp,
    val gapInline: Dp = 4.dp,
    val gapRow: Dp = 8.dp,
    val gapSection: Dp = 48.dp,
    val gapPage: Dp = 64.dp,
    val padPage: Dp = 32.dp,
)

val frostSpacingDefault = FrostSpacing()
