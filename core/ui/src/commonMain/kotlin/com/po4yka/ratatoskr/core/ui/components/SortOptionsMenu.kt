package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.frost.BracketIconButton
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.domain.model.SortOrder
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.sort_menu_alphabetical
import ratatoskr.core.ui.generated.resources.sort_menu_description
import ratatoskr.core.ui.generated.resources.sort_menu_newest
import ratatoskr.core.ui.generated.resources.sort_menu_oldest
import ratatoskr.core.ui.generated.resources.sort_menu_selected

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
        BracketIconButton(
            onClick = { expanded = true },
            contentDescription = stringResource(Res.string.sort_menu_description, currentSortOrder.displayName()),
        ) {
            FrostIcon(
                imageVector = AppIcons.SortAscending,
                contentDescription = null,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        AppMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp),
        ) {
            SortOrder.entries.forEach { sortOrder ->
                AppMenuItem(
                    label = sortOrder.displayName(),
                    onClick = {
                        onSortOrderSelected(sortOrder)
                        expanded = false
                    },
                    trailingContent =
                        if (currentSortOrder == sortOrder) {
                            {
                                FrostIcon(
                                    imageVector = AppIcons.Checkmark,
                                    contentDescription = stringResource(Res.string.sort_menu_selected),
                                    tint = AppTheme.frostColors.ink,
                                    modifier = Modifier.size(IconSizes.xs),
                                )
                            }
                        } else {
                            null
                        },
                )
            }
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
