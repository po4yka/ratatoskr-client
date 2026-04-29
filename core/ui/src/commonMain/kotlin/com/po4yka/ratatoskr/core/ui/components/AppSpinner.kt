package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

/**
 * Project-owned spinner shim (48 dp default).
 *
 * Backs onto Material 3 [CircularProgressIndicator]; introduced as a thin wrapper so call sites
 * can swap the backing impl without re-touching each one. Pass `Modifier.size(...)` to override
 * the diameter — the modifier wins because it is applied after the default.
 */
@Composable
fun AppSpinner(modifier: Modifier = Modifier.size(48.dp)) {
    CircularProgressIndicator(
        modifier = modifier,
        color = AppTheme.colors.interactive,
    )
}

/**
 * Smaller spinner variant (16 dp). Stroke width is reduced so the smaller diameter still reads
 * as a spinner.
 */
@Composable
fun AppSmallSpinner(modifier: Modifier = Modifier.size(16.dp)) {
    CircularProgressIndicator(
        modifier = modifier,
        color = AppTheme.colors.interactive,
        strokeWidth = 2.dp,
    )
}
