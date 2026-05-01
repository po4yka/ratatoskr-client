package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton

// TODO: callers should migrate to BracketButton directly;
//   AppTextButton is a transitional shim
@Composable
fun AppTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @Suppress("UNUSED_PARAMETER") contentPadding: PaddingValues =
        PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp,
        ),
) {
    BracketButton(
        label = label,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    )
}
