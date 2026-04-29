package com.po4yka.ratatoskr.feature.digest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import com.po4yka.ratatoskr.core.ui.components.AppSmallSpinner
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.DigestFormat
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.presentation.navigation.CustomDigestCreateComponent
import com.po4yka.ratatoskr.core.ui.components.AppCheckbox
import com.po4yka.ratatoskr.core.ui.components.SelectableChip
import com.po4yka.ratatoskr.core.ui.components.ScreenHeader
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.custom_digest_create_creating
import ratatoskr.core.ui.generated.resources.custom_digest_create_detailed
import ratatoskr.core.ui.generated.resources.custom_digest_create_generate
import ratatoskr.core.ui.generated.resources.custom_digest_create_no_articles
import ratatoskr.core.ui.generated.resources.custom_digest_create_read_time
import ratatoskr.core.ui.generated.resources.custom_digest_create_search_label
import ratatoskr.core.ui.generated.resources.custom_digest_create_selected_count
import ratatoskr.core.ui.generated.resources.custom_digest_create_title
import ratatoskr.core.ui.generated.resources.custom_digest_create_title_label
import ratatoskr.core.ui.generated.resources.custom_digest_create_title_placeholder
import ratatoskr.core.ui.generated.resources.custom_digest_create_brief
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun CustomDigestCreateScreen(
    component: CustomDigestCreateComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.createdDigestId) {
        state.createdDigestId?.let { component.onDigestCreated(it) }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
    ) {
        ScreenHeader(
            title = stringResource(Res.string.custom_digest_create_title),
            isDetailScreen = true,
            onBackClick = component::onBackClicked,
        )

        HorizontalDivider(color = AppTheme.colors.borderSubtle00)

        // Title input
        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.setTitle(it) },
            label = { Text(stringResource(Res.string.custom_digest_create_title_label)) },
            placeholder = { Text(stringResource(Res.string.custom_digest_create_title_placeholder)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )

        // Format toggle: Brief / Detailed
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            FormatChip(
                label = stringResource(Res.string.custom_digest_create_brief),
                isSelected = state.format == DigestFormat.BRIEF,
                onClick = { viewModel.setFormat(DigestFormat.BRIEF) },
            )
            FormatChip(
                label = stringResource(Res.string.custom_digest_create_detailed),
                isSelected = state.format == DigestFormat.DETAILED,
                onClick = { viewModel.setFormat(DigestFormat.DETAILED) },
            )
        }

        // Search bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchChanged(it) },
            label = { Text(stringResource(Res.string.custom_digest_create_search_label)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        )

        // Summary list
        if (state.isLoadingSummaries) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                AppSmallSpinner()
            }
        } else {
            val filtered = state.filteredSummaries
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = Spacing.sm),
            ) {
                items(filtered, key = { it.id }) { summary ->
                    SelectableSummaryRow(
                        summary = summary,
                        isSelected = summary.id in state.selectedIds,
                        onClick = { viewModel.toggleSelection(summary.id) },
                    )
                    HorizontalDivider(color = AppTheme.colors.borderSubtle00)
                }
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(Res.string.custom_digest_create_no_articles),
                                style = AppTheme.type.bodyCompact01,
                                color = AppTheme.colors.textSecondary,
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = AppTheme.colors.borderSubtle00)

        // Bottom bar
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.layer01)
                    .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.custom_digest_create_selected_count, state.selectedIds.size),
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textSecondary,
            )
            Button(
                onClick = { viewModel.createDigest() },
                enabled = state.selectedIds.isNotEmpty() && !state.isCreating,
            ) {
                Text(
                    if (state.isCreating) {
                        stringResource(Res.string.custom_digest_create_creating)
                    } else {
                        stringResource(Res.string.custom_digest_create_generate)
                    },
                )
            }
        }

        // Error message
        state.error?.let { errorText ->
            Text(
                text = errorText,
                style = AppTheme.type.label01,
                color = AppTheme.colors.supportError,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun FormatChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    SelectableChip(
        label = label,
        selected = isSelected,
        onClick = onClick,
    )
}

@Suppress("FunctionNaming")
@Composable
private fun SelectableSummaryRow(
    summary: Summary,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .background(if (isSelected) AppTheme.colors.layer02 else AppTheme.colors.background)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppCheckbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = summary.title,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            summary.readingTimeMin?.let { readTime ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.custom_digest_create_read_time, readTime),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                )
            }
        }
    }
}
