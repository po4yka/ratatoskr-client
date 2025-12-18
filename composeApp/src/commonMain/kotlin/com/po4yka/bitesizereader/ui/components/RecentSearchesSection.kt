package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons

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

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(16.dp),
    ) {
        // Header with Clear All button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recent Searches",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )

            TextButton(
                onClick = onClearAll,
            ) {
                Text(
                    text = "Clear All",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.linkPrimary,
                )
            }
        }

        // Search items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = CarbonIcons.Search,
                contentDescription = null,
                tint = Carbon.theme.iconSecondary,
                modifier = Modifier.size(16.dp),
            )

            Text(
                text = query,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = CarbonIcons.Close,
                contentDescription = "Remove $query from recent searches",
                tint = Carbon.theme.iconSecondary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
