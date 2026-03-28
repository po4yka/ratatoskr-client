package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.sort_menu_alphabetical
import bitesizereader.composeapp.generated.resources.sort_menu_description
import bitesizereader.composeapp.generated.resources.sort_menu_newest
import bitesizereader.composeapp.generated.resources.sort_menu_oldest
import bitesizereader.composeapp.generated.resources.sort_menu_selected
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.SortOrder
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

/**
 * Sort options menu with trigger button and dropdown.
 * Manages its own expanded state internally.
 */
@Suppress("FunctionNaming")
@Composable
fun SortOptionsMenu(
    currentSortOrder: SortOrder,
    onSortOrderSelected: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        CarbonIconButton(
            imageVector = CarbonIcons.SortAscending,
            contentDescription = stringResource(Res.string.sort_menu_description, currentSortOrder.displayName()),
            onClick = { expanded = true },
        )

        CarbonMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier
                    .width(Dimensions.menuWidth)
                    .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                    .background(Carbon.theme.layer01),
        ) {
            Column {
                SortOrder.entries.forEach { sortOrder ->
                    SortOptionItem(
                        sortOrder = sortOrder,
                        isSelected = currentSortOrder == sortOrder,
                        onClick = {
                            onSortOrderSelected(sortOrder)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SortOptionItem(
    sortOrder: SortOrder,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = sortOrder.displayName(),
            style = Carbon.typography.bodyCompact01,
            color = if (isSelected) Carbon.theme.textPrimary else Carbon.theme.textSecondary,
            modifier = Modifier.weight(1f),
        )

        if (isSelected) {
            Icon(
                imageVector = CarbonIcons.Checkmark,
                contentDescription = stringResource(Res.string.sort_menu_selected),
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.xs),
            )
        }
    }
}

@Composable
private fun SortOrder.displayName(): String =
    when (this) {
        SortOrder.NEWEST -> stringResource(Res.string.sort_menu_newest)
        SortOrder.OLDEST -> stringResource(Res.string.sort_menu_oldest)
        SortOrder.ALPHABETICAL -> stringResource(Res.string.sort_menu_alphabetical)
    }
