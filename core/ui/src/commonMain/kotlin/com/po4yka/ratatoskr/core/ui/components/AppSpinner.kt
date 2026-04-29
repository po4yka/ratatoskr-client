package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

/**
 * Project-owned spinner shim, sized to roughly match Carbon's `Loading` size.
 *
 * Backs onto Material 3 [CircularProgressIndicator] today; introduced as a thin wrapper so the
 * 21 call sites that previously used Carbon's `Loading` / `SmallLoading` can swap backing impl
 * without re-touching call sites.
 */
@Composable
fun AppSpinner(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(48.dp),
        color = AppTheme.colors.interactive,
    )
}

/**
 * Smaller spinner variant, sized to roughly match Carbon's `SmallLoading` size.
 *
 * Stroke width is reduced so the smaller diameter still reads as a spinner.
 */
@Composable
fun AppSmallSpinner(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(16.dp),
        color = AppTheme.colors.interactive,
        strokeWidth = 2.dp,
    )
}
