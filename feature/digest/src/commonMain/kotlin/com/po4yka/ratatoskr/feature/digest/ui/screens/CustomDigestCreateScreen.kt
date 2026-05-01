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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.frost.BracketField
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDivider
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.components.frost.FrostCheckbox
import com.po4yka.ratatoskr.core.ui.components.frost.FrostSpinner
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.DigestFormat
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.presentation.navigation.CustomDigestCreateComponent
import com.po4yka.ratatoskr.core.ui.components.ScreenHeader
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
                .background(AppTheme.frostColors.page),
    ) {
        ScreenHeader(
            title = stringResource(Res.string.custom_digest_create_title),
            isDetailScreen = true,
            onBackClick = component::onBackClicked,
        )

        FrostDivider()

        // Title input
        BracketField(
            value = state.title,
            onValueChange = { viewModel.setTitle(it) },
            label = stringResource(Res.string.custom_digest_create_title_label),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell),
        )

        // Format toggle: Brief / Detailed
        Row(
            modifier = Modifier.padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
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
        BracketField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchChanged(it) },
            label = stringResource(Res.string.custom_digest_create_search_label),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell),
        )

        // Summary list
        if (state.isLoadingSummaries) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                FrostSpinner(size = 16.dp)
            }
        } else {
            val filtered = state.filteredSummaries
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = AppTheme.spacing.cell),
            ) {
                items(filtered, key = { it.id }) { summary ->
                    SelectableSummaryRow(
                        summary = summary,
                        isSelected = summary.id in state.selectedIds,
                        onClick = { viewModel.toggleSelection(summary.id) },
                    )
                    FrostDivider()
                }
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            FrostText(
                                text = stringResource(Res.string.custom_digest_create_no_articles),
                                style = AppTheme.frostType.monoBody,
                                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                            )
                        }
                    }
                }
            }
        }

        FrostDivider()

        // Bottom bar
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(AppTheme.frostColors.page)
                    .padding(AppTheme.spacing.line),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FrostText(
                text = stringResource(Res.string.custom_digest_create_selected_count, state.selectedIds.size),
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
            BracketButton(
                label =
                    if (state.isCreating) {
                        stringResource(Res.string.custom_digest_create_creating)
                    } else {
                        stringResource(Res.string.custom_digest_create_generate)
                    },
                onClick = { viewModel.createDigest() },
                enabled = state.selectedIds.isNotEmpty() && !state.isCreating,
            )
        }

        // Error message
        state.error?.let { errorText ->
            FrostText(
                text = errorText,
                style = AppTheme.frostType.monoXs,
                color = AppTheme.frostColors.spark,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell),
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
    MultiSelectChip(
        label = label,
        selected = isSelected,
        onToggle = onClick,
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
                .background(if (isSelected) AppTheme.frostColors.page else AppTheme.frostColors.page)
                .padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FrostCheckbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
        )

        Spacer(modifier = Modifier.width(AppTheme.spacing.cell))

        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = summary.title,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            summary.readingTimeMin?.let { readTime ->
                Spacer(modifier = Modifier.height(2.dp))
                FrostText(
                    text = stringResource(Res.string.custom_digest_create_read_time, readTime),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                )
            }
        }
    }
}
