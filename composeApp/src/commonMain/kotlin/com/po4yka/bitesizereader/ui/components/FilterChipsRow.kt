package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.presentation.state.ReadFilter
import com.po4yka.bitesizereader.ui.theme.Spacing

@Suppress("FunctionNaming", "LongParameterList")
@Composable
fun FilterChipsRow(
    readFilter: ReadFilter,
    onReadFilterChange: (ReadFilter) -> Unit,
    availableTags: List<String>,
    selectedTag: String?,
    onTagSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.padding(vertical = Spacing.sm),
        contentPadding = PaddingValues(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Read filter chips
        items(ReadFilter.entries.toTypedArray()) { filter ->
            FilterChip(
                label = filter.displayName(),
                isSelected = readFilter == filter,
                onClick = { onReadFilterChange(filter) },
            )
        }

        // Tag chips
        if (availableTags.isNotEmpty()) {
            items(availableTags) { tag ->
                FilterChip(
                    label = tag,
                    isSelected = selectedTag == tag,
                    onClick = {
                        onTagSelected(if (selectedTag == tag) null else tag)
                    },
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Spacing.md)
    val backgroundColor =
        if (isSelected) {
            Carbon.theme.backgroundInverse
        } else {
            Carbon.theme.layer01
        }
    val textColor =
        if (isSelected) {
            Carbon.theme.textOnColor
        } else {
            Carbon.theme.textSecondary
        }
    val borderColor =
        if (isSelected) {
            Carbon.theme.backgroundInverse
        } else {
            Carbon.theme.borderSubtle00
        }

    Box(
        modifier =
            modifier
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                .semantics {
                    role = Role.Tab
                    selected = isSelected
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = Carbon.typography.bodyCompact01,
            color = textColor,
        )
    }
}

private fun ReadFilter.displayName(): String =
    when (this) {
        ReadFilter.ALL -> "All"
        ReadFilter.UNREAD -> "Unread"
        ReadFilter.READ -> "Read"
    }
