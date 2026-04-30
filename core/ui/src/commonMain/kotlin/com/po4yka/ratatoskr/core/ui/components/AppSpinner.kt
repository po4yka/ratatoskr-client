package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.frost.FrostSpinner

/**
 * Project-owned spinner shim (48 dp default).
 *
 * Backs onto Frost [FrostSpinner]; introduced as a thin wrapper so call sites
 * can swap the backing impl without re-touching each one.
 *
 * TODO: callers should migrate to FrostSpinner directly; AppSpinner is a transitional shim
 */
@Composable
fun AppSpinner(modifier: Modifier = Modifier) {
    FrostSpinner(
        modifier = modifier,
        size = 48.dp,
    )
}

/**
 * Smaller spinner variant (16 dp).
 *
 * TODO: callers should migrate to FrostSpinner directly; AppSmallSpinner is a transitional shim
 */
@Composable
fun AppSmallSpinner(modifier: Modifier = Modifier) {
    FrostSpinner(
        modifier = modifier,
        size = 16.dp,
    )
}
