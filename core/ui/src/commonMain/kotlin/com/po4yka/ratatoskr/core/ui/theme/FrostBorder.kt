package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Frost border tokens.
 *
 * 0 corner radius everywhere. Borders are hairlines or spark bars only.
 * Apply alpha values to [FrostColors.ink] for divider/separator colors.
 */
@Immutable
data class FrostBorder(
    val hairline: Dp = 1.dp,
    val sparkBar: Dp = 4.dp,
    val separatorAlpha: Float = 0.40f,
    val rowDividerAlpha: Float = 0.50f,
    val focusAlpha: Float = 1.00f,
)

val frostBorderDefault = FrostBorder()
