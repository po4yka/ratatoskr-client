package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip

// TODO: callers should migrate to MultiSelectChip directly;
//   SelectableChip is a transitional shim
@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @Suppress("UNUSED_PARAMETER") role: Role = Role.RadioButton,
    @Suppress("UNUSED_PARAMETER") contentPadding: PaddingValues =
        PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp,
        ),
) {
    MultiSelectChip(
        label = label,
        selected = selected,
        onToggle = onClick,
        enabled = enabled,
        modifier = modifier,
    )
}
