package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.sort_menu_alphabetical
import ratatoskr.core.ui.generated.resources.sort_menu_description
import ratatoskr.core.ui.generated.resources.sort_menu_newest
import ratatoskr.core.ui.generated.resources.sort_menu_oldest
import ratatoskr.core.ui.generated.resources.sort_menu_selected
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
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
        AppIconButton(
            imageVector = AppIcons.SortAscending,
            contentDescription = stringResource(Res.string.sort_menu_description, currentSortOrder.displayName()),
            onClick = { expanded = true },
        )

        AppMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(Dimensions.menuWidth),
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
                                Icon(
                                    imageVector = AppIcons.Checkmark,
                                    contentDescription = stringResource(Res.string.sort_menu_selected),
                                    tint = AppTheme.colors.iconPrimary,
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
