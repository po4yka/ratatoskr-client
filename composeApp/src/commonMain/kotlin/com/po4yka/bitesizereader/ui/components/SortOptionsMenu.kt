package com.po4yka.bitesizereader.ui.components

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
            modifier = Modifier.width(Dimensions.menuWidth),
        ) {
            SortOrder.entries.forEach { sortOrder ->
                CarbonMenuItem(
                    label = sortOrder.displayName(),
                    onClick = {
                        onSortOrderSelected(sortOrder)
                        expanded = false
                    },
                    trailingContent =
                        if (currentSortOrder == sortOrder) {
                            {
                                Icon(
                                    imageVector = CarbonIcons.Checkmark,
                                    contentDescription = stringResource(Res.string.sort_menu_selected),
                                    tint = Carbon.theme.iconPrimary,
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
