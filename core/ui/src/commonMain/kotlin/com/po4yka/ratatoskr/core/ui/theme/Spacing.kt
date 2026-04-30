package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Spacing scale aligned to the Frost 8dp cell grid.
 * Slot names are migration aliases — new code should use AppTheme.spacing (FrostSpacing) instead.
 *
 * Changes from previous scale:
 *   sm: 12dp -> 8dp (snapped to cell grid)
 */
object Spacing {
    /** 4.dp - Tight spacing for compact elements */
    val xxs = 4.dp

    /** 8.dp - Internal component spacing */
    val xs = 8.dp

    /** 8.dp - Item spacing in lists (was 12dp; snapped to Frost cell grid) */
    val sm = 8.dp

    /** 16.dp - Standard content padding */
    val md = 16.dp

    /** 24.dp - Section spacing */
    val lg = 24.dp

    /** 32.dp - Large gaps between major sections */
    val xl = 32.dp

    /** 48.dp - Extra large spacing for empty states */
    val xxl = 48.dp
}
