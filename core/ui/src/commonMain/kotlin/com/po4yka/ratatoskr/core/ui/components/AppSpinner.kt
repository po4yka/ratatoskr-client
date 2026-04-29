package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

/**
 * Project-owned spinner shim, sized to roughly match Carbon's `Loading` (48 dp default).
 *
 * Backs onto Material 3 [CircularProgressIndicator] today; introduced as a thin wrapper so the
 * 21 call sites that previously used Carbon's `Loading` / `SmallLoading` can swap backing impl
 * without re-touching call sites. Call sites that want a different diameter pass
 * `Modifier.size(...)` — the modifier wins because it's applied after the default.
 */
@Composable
fun AppSpinner(modifier: Modifier = Modifier.size(48.dp)) {
    CircularProgressIndicator(
        modifier = modifier,
        color = AppTheme.colors.interactive,
    )
}

/**
 * Smaller spinner variant, sized to roughly match Carbon's `SmallLoading` (16 dp).
 *
 * Stroke width is reduced so the smaller diameter still reads as a spinner.
 */
@Composable
fun AppSmallSpinner(modifier: Modifier = Modifier.size(16.dp)) {
    CircularProgressIndicator(
        modifier = modifier,
        color = AppTheme.colors.interactive,
        strokeWidth = 2.dp,
    )
}
