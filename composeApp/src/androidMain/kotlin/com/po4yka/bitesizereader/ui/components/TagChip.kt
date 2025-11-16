package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Chip component for displaying topic tags
 */
@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    if (onClick != null) {
        FilterChip(
            selected = false,
            onClick = onClick,
            label = {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            modifier = modifier
        )
    } else {
        SuggestionChip(
            onClick = { },
            label = {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            modifier = modifier
        )
    }
}

/**
 * Selectable tag chip for filters
 */
@Composable
fun SelectableTagChip(
    tag: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = modifier
    )
}
