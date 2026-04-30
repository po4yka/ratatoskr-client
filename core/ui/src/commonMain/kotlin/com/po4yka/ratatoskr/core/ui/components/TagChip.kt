package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip

// TODO: callers should migrate to MultiSelectChip(selected = false) directly;
//   TagChip is a transitional shim for read-only tag display
@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier,
) {
    MultiSelectChip(
        label = tag,
        selected = false,
        onToggle = {},
        enabled = false,
        modifier = modifier,
    )
}
