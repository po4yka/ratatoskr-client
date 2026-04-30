package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.frost.FrostCheckbox

// TODO: callers should migrate to FrostCheckbox directly; AppCheckbox is a transitional shim
@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FrostCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
    )
}
