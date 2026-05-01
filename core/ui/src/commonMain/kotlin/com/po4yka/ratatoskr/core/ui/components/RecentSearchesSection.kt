package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.recent_searches_clear_all
import ratatoskr.core.ui.generated.resources.recent_searches_remove
import ratatoskr.core.ui.generated.resources.recent_searches_title

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

    val ink = AppTheme.frostColors.ink

    BrutalistCard(
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
                SectionHeading(
                    text = stringResource(Res.string.recent_searches_title),
                    modifier = Modifier.weight(1f),
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
    val ink = AppTheme.frostColors.ink

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
            FrostIcon(
                imageVector = AppIcons.Search,
                contentDescription = null,
                tint = ink.copy(alpha = AppTheme.alpha.secondary),
                modifier = Modifier.size(IconSizes.xs),
            )
            FrostText(
                text = query,
                style = AppTheme.frostType.monoBody,
                color = ink,
            )
        }

        AppIconButton(
            imageVector = AppIcons.Close,
            contentDescription = stringResource(Res.string.recent_searches_remove, query),
            onClick = onDelete,
            buttonSize = Dimensions.compactIconButtonSize,
            iconSize = IconSizes.xs,
            tint = ink.copy(alpha = AppTheme.alpha.secondary),
        )
    }
}
