package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.ReadFilter
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.filter_chip_archived
import ratatoskr.core.ui.generated.resources.filter_chip_favorites
import ratatoskr.core.ui.generated.resources.search_filter_all
import ratatoskr.core.ui.generated.resources.search_filter_read
import ratatoskr.core.ui.generated.resources.search_filter_unread

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
            MultiSelectChip(
                label = filter.displayName(),
                selected = readFilter == filter,
                onToggle = { onReadFilterChange(filter) },
            )
        }

        // Tag chips
        if (availableTags.isNotEmpty()) {
            items(availableTags) { tag ->
                MultiSelectChip(
                    label = tag,
                    selected = selectedTag == tag,
                    onToggle = { onTagSelected(if (selectedTag == tag) null else tag) },
                )
            }
        }
    }
}

@Composable
private fun ReadFilter.displayName(): String =
    when (this) {
        ReadFilter.ALL -> stringResource(Res.string.search_filter_all)
        ReadFilter.UNREAD -> stringResource(Res.string.search_filter_unread)
        ReadFilter.READ -> stringResource(Res.string.search_filter_read)
        ReadFilter.FAVORITED -> stringResource(Res.string.filter_chip_favorites)
        ReadFilter.ARCHIVED -> stringResource(Res.string.filter_chip_archived)
    }
