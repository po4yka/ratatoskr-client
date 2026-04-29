package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.recent_searches_clear_all
import ratatoskr.core.ui.generated.resources.recent_searches_remove
import ratatoskr.core.ui.generated.resources.recent_searches_title
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.icons.CarbonIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

/**
 * Section that displays recent search queries with ability to reuse or delete them.
 * Shown when search is active but the query is empty.
 */
@Suppress("FunctionNaming")
@Composable
fun RecentSearchesSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onDeleteSearch: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (searches.isEmpty()) return

    LayerCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.xxs),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
        ) {
            // Header with Clear All button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.recent_searches_title),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                )

                AppTextButton(
                    label = stringResource(Res.string.recent_searches_clear_all),
                    onClick = onClearAll,
                )
            }

            // Search items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                searches.forEach { query ->
                    RecentSearchItem(
                        query = query,
                        onClick = { onSearchClick(query) },
                        onDelete = { onDeleteSearch(query) },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RecentSearchItem(
    query: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .semantics { role = Role.Button }
                .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = CarbonIcons.Search,
                contentDescription = null,
                tint = AppTheme.colors.iconSecondary,
                modifier = Modifier.size(IconSizes.xs),
            )

            Text(
                text = query,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textPrimary,
            )
        }

        AppIconButton(
            imageVector = CarbonIcons.Close,
            contentDescription = stringResource(Res.string.recent_searches_remove, query),
            onClick = onDelete,
            buttonSize = Dimensions.compactIconButtonSize,
            iconSize = IconSizes.xs,
            tint = AppTheme.colors.iconSecondary,
        )
    }
}
