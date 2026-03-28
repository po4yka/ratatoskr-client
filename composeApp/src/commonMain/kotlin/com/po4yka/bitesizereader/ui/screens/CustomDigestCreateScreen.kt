package com.po4yka.bitesizereader.ui.screens

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
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.domain.model.DigestFormat
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.navigation.CustomDigestCreateComponent
import com.po4yka.bitesizereader.ui.components.CarbonCheckbox
import com.po4yka.bitesizereader.ui.components.CarbonSelectableChip
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.custom_digest_create_creating
import bitesizereader.composeapp.generated.resources.custom_digest_create_detailed
import bitesizereader.composeapp.generated.resources.custom_digest_create_generate
import bitesizereader.composeapp.generated.resources.custom_digest_create_no_articles
import bitesizereader.composeapp.generated.resources.custom_digest_create_read_time
import bitesizereader.composeapp.generated.resources.custom_digest_create_search_label
import bitesizereader.composeapp.generated.resources.custom_digest_create_selected_count
import bitesizereader.composeapp.generated.resources.custom_digest_create_title
import bitesizereader.composeapp.generated.resources.custom_digest_create_title_label
import bitesizereader.composeapp.generated.resources.custom_digest_create_title_placeholder
import bitesizereader.composeapp.generated.resources.custom_digest_create_brief
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
                .background(Carbon.theme.background),
    ) {
        ScreenHeader(
            title = stringResource(Res.string.custom_digest_create_title),
            isDetailScreen = true,
            onBackClick = component::onBackClicked,
        )

        HorizontalDivider(color = Carbon.theme.borderSubtle00)

        // Title input
        TextInput(
            label = stringResource(Res.string.custom_digest_create_title_label),
            value = state.title,
            onValueChange = { viewModel.setTitle(it) },
            placeholderText = stringResource(Res.string.custom_digest_create_title_placeholder),
            state = TextInputState.Enabled,
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
        TextInput(
            label = stringResource(Res.string.custom_digest_create_search_label),
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchChanged(it) },
            state = TextInputState.Enabled,
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
                SmallLoading()
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
                    HorizontalDivider(color = Carbon.theme.borderSubtle00)
                }
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(Res.string.custom_digest_create_no_articles),
                                style = Carbon.typography.bodyCompact01,
                                color = Carbon.theme.textSecondary,
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Carbon.theme.borderSubtle00)

        // Bottom bar
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Carbon.theme.layer01)
                    .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.custom_digest_create_selected_count, state.selectedIds.size),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
            Button(
                label =
                    if (state.isCreating) {
                        stringResource(Res.string.custom_digest_create_creating)
                    } else {
                        stringResource(Res.string.custom_digest_create_generate)
                    },
                onClick = { viewModel.createDigest() },
                isEnabled = state.selectedIds.isNotEmpty() && !state.isCreating,
                buttonType = ButtonType.Primary,
            )
        }

        // Error message
        state.error?.let { errorText ->
            Text(
                text = errorText,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
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
    CarbonSelectableChip(
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
                .background(if (isSelected) Carbon.theme.layer02 else Carbon.theme.background)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CarbonCheckbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = summary.title,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            summary.readingTimeMin?.let { readTime ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.custom_digest_create_read_time, readTime),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }
    }
}
