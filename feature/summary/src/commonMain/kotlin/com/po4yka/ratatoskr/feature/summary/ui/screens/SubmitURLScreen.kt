package com.po4yka.ratatoskr.feature.summary.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.RectangleShape
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import com.po4yka.ratatoskr.core.ui.components.frost.BracketField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.domain.model.BatchUrlEntry
import com.po4yka.ratatoskr.domain.model.BatchUrlStatus
import com.po4yka.ratatoskr.domain.model.ProcessingStage
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.RequestStatus
import com.po4yka.ratatoskr.presentation.navigation.SubmitURLComponent
import com.po4yka.ratatoskr.presentation.state.SubmitURLState
import com.po4yka.ratatoskr.presentation.state.SubmitUrlError
import com.po4yka.ratatoskr.core.ui.components.frost.BracketIconButton
import com.po4yka.ratatoskr.core.ui.components.frost.FrostSpinner
import com.po4yka.ratatoskr.core.ui.components.TextArea
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.submit_url_back
import ratatoskr.core.ui.generated.resources.submit_url_batch_cancel
import ratatoskr.core.ui.generated.resources.submit_url_batch_completed
import ratatoskr.core.ui.generated.resources.submit_url_batch_detected_plural
import ratatoskr.core.ui.generated.resources.submit_url_batch_detected_singular
import ratatoskr.core.ui.generated.resources.submit_url_batch_duplicate_skipped
import ratatoskr.core.ui.generated.resources.submit_url_batch_failed
import ratatoskr.core.ui.generated.resources.submit_url_batch_pending
import ratatoskr.core.ui.generated.resources.submit_url_batch_placeholder
import ratatoskr.core.ui.generated.resources.submit_url_batch_progress
import ratatoskr.core.ui.generated.resources.submit_url_batch_prompt
import ratatoskr.core.ui.generated.resources.submit_url_batch_retry
import ratatoskr.core.ui.generated.resources.submit_url_batch_skipped
import ratatoskr.core.ui.generated.resources.submit_url_batch_submit_all
import ratatoskr.core.ui.generated.resources.submit_url_batch_submit_anyway
import ratatoskr.core.ui.generated.resources.submit_url_batch_submit_more
import ratatoskr.core.ui.generated.resources.submit_url_cancel
import ratatoskr.core.ui.generated.resources.submit_url_duplicate_message
import ratatoskr.core.ui.generated.resources.submit_url_duplicate_title
import ratatoskr.core.ui.generated.resources.submit_url_duplicate_warning
import ratatoskr.core.ui.generated.resources.submit_url_enter_prompt
import ratatoskr.core.ui.generated.resources.submit_url_hide_history
import ratatoskr.core.ui.generated.resources.submit_url_label
import ratatoskr.core.ui.generated.resources.submit_url_mode_batch
import ratatoskr.core.ui.generated.resources.submit_url_mode_single
import ratatoskr.core.ui.generated.resources.submit_url_no_recent_requests
import ratatoskr.core.ui.generated.resources.submit_url_processing
import ratatoskr.core.ui.generated.resources.submit_url_processing_default_message
import ratatoskr.core.ui.generated.resources.submit_url_processing_stage
import ratatoskr.core.ui.generated.resources.submit_url_request_history
import ratatoskr.core.ui.generated.resources.submit_url_request_status_completed
import ratatoskr.core.ui.generated.resources.submit_url_request_status_failed
import ratatoskr.core.ui.generated.resources.submit_url_request_status_pending
import ratatoskr.core.ui.generated.resources.submit_url_request_status_processing
import ratatoskr.core.ui.generated.resources.submit_url_show_history
import ratatoskr.core.ui.generated.resources.submit_url_submit
import ratatoskr.core.ui.generated.resources.submit_url_submit_anyway
import ratatoskr.core.ui.generated.resources.submit_url_submitting
import ratatoskr.core.ui.generated.resources.submit_url_success
import ratatoskr.core.ui.generated.resources.submit_url_title
import ratatoskr.core.ui.generated.resources.submit_url_view_existing
import ratatoskr.core.ui.generated.resources.submit_url_error_invalid_url
import ratatoskr.core.ui.generated.resources.submit_url_error_duplicate
import ratatoskr.core.ui.generated.resources.submit_url_error_network
import ratatoskr.core.ui.generated.resources.submit_url_error_server
import ratatoskr.core.ui.generated.resources.submit_url_error_view_library
import ratatoskr.core.ui.generated.resources.settings_retry
import ratatoskr.core.ui.generated.resources.common_percent
import ratatoskr.core.ui.generated.resources.processing_stage_done
import ratatoskr.core.ui.generated.resources.processing_stage_extraction
import ratatoskr.core.ui.generated.resources.processing_stage_queued
import ratatoskr.core.ui.generated.resources.processing_stage_saving
import ratatoskr.core.ui.generated.resources.processing_stage_summarization
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun SubmitURLScreen(
    component: SubmitURLComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.frostColors.page),
    ) {
        // Header with back button
        SubmitURLHeader(
            onBackClick = component::onBackClicked,
        )

        // Main content
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppTheme.spacing.line),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.line),
        ) {
            // Mode toggle
            item {
                Spacer(modifier = Modifier.height(AppTheme.spacing.cell))
                BatchModeToggle(
                    isBatchMode = state.isBatchMode,
                    onToggle = { viewModel.toggleBatchMode() },
                )
            }

            if (!state.isBatchMode) {
                // URL Input section (single mode)
                item {
                    URLInputSection(
                        url = state.url,
                        onUrlChanged = viewModel::onUrlChanged,
                        onSubmit = { viewModel.checkDuplicate() },
                        isLoading = state.isLoading || state.isCheckingDuplicate,
                        error = state.error,
                        submitError = state.submitError,
                        onViewLibrary = component::onBackClicked,
                    )
                }

                // Duplicate warning section
                item {
                    AnimatedVisibility(
                        visible = state.isDuplicate,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        DuplicateWarningSection(
                            summaryId = state.duplicateSummaryId,
                            onViewExisting = { summaryId ->
                                summaryId?.let { component.onViewExistingSummary(it) }
                            },
                            onForceSubmit = { viewModel.forceSubmit() },
                            onDismiss = { viewModel.dismissDuplicate() },
                        )
                    }
                }

                // Progress section
                item {
                    AnimatedVisibility(
                        visible = state.isLoading,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        SubmissionProgressSection(state = state)
                    }
                }

                // Completion message
                item {
                    AnimatedVisibility(
                        visible = state.status == RequestStatus.COMPLETED,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        CompletionSection()
                    }
                }
            } else {
                // Batch input section (shown when no entries yet)
                if (state.batchEntries.isEmpty()) {
                    item {
                        BatchInputSection(
                            batchInput = state.batchInput,
                            onBatchInputChanged = viewModel::onBatchInputChanged,
                            onSubmitBatch = { viewModel.submitBatch() },
                        )
                    }
                } else {
                    // Batch results section (shown during and after submission)
                    item {
                        BatchProgressHeader(
                            completedCount = state.batchCompletedCount,
                            totalCount = state.batchEntries.size,
                            isBatchSubmitting = state.isBatchSubmitting,
                            onCancel = { viewModel.cancelBatch() },
                            onReset = { viewModel.toggleBatchMode() },
                        )
                    }

                    itemsIndexed(
                        items = state.batchEntries,
                        key = { _, entry -> entry.url },
                    ) { index, entry ->
                        BatchUrlEntryRow(
                            entry = entry,
                            onRetry = { viewModel.retryBatchEntry(index) },
                            onSkip = { viewModel.skipBatchEntry(index) },
                            onSubmitAnyway = { viewModel.submitBatchEntryAnyway(index) },
                        )
                    }
                }
            }

            // Request history section
            item {
                Spacer(modifier = Modifier.height(AppTheme.spacing.line))
                RequestHistoryHeader(
                    showHistory = state.showHistory,
                    onToggle = { viewModel.toggleHistoryVisibility() },
                )
            }

            if (state.showHistory) {
                if (state.isLoadingHistory) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            FrostSpinner(size = 16.dp)
                        }
                    }
                } else if (state.recentRequests.isEmpty()) {
                    item {
                        FrostText(
                            text = stringResource(Res.string.submit_url_no_recent_requests),
                            style = AppTheme.frostType.monoBody,
                            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                            modifier = Modifier.padding(vertical = AppTheme.spacing.line),
                        )
                    }
                } else {
                    items(
                        items = state.recentRequests,
                        key = { it.id },
                    ) { request ->
                        RequestHistoryItem(
                            request = request,
                            onRetry = { viewModel.retryRequest(request) },
                        )
                    }
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(AppTheme.spacing.padPage))
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun BatchModeToggle(
    isBatchMode: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        ModeChip(
            label = stringResource(Res.string.submit_url_mode_single),
            isSelected = !isBatchMode,
            onClick = { if (isBatchMode) onToggle() },
        )
        ModeChip(
            label = stringResource(Res.string.submit_url_mode_batch),
            isSelected = isBatchMode,
            onClick = { if (!isBatchMode) onToggle() },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RectangleShape
    val backgroundColor = if (isSelected) AppTheme.frostColors.ink else AppTheme.frostColors.page
    val textColor =
        if (isSelected) {
            AppTheme.frostColors.page
        } else {
            AppTheme.frostColors.ink.copy(
                alpha = AppTheme.alpha.secondary,
            )
        }
    val borderColor =
        if (isSelected) {
            AppTheme.frostColors.ink
        } else {
            AppTheme.frostColors.ink.copy(
                alpha = AppTheme.border.separatorAlpha,
            )
        }

    Box(
        modifier =
            Modifier
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell)
                .semantics {
                    role = Role.Tab
                    selected = isSelected
                },
        contentAlignment = Alignment.Center,
    ) {
        FrostText(
            text = label,
            style = AppTheme.frostType.monoBody,
            color = textColor,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun BatchInputSection(
    batchInput: String,
    onBatchInputChanged: (String) -> Unit,
    onSubmitBatch: () -> Unit,
) {
    val urlCount =
        batchInput.lines()
            .map { it.trim() }
            .count { it.startsWith("http://") || it.startsWith("https://") }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        FrostText(
            text = stringResource(Res.string.submit_url_batch_prompt),
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )

        TextArea(
            value = batchInput,
            onValueChange = onBatchInputChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholderText = stringResource(Res.string.submit_url_batch_placeholder),
            minHeight = 180.dp,
        )

        FrostText(
            text =
                if (urlCount == 1) {
                    stringResource(Res.string.submit_url_batch_detected_singular, urlCount)
                } else {
                    stringResource(Res.string.submit_url_batch_detected_plural, urlCount)
                },
            style = AppTheme.frostType.monoXs,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )

        BracketButton(
            label = stringResource(Res.string.submit_url_batch_submit_all),
            onClick = onSubmitBatch,
            enabled = urlCount > 0,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun BatchProgressHeader(
    completedCount: Int,
    totalCount: Int,
    isBatchSubmitting: Boolean,
    onCancel: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.line),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FrostText(
            text = stringResource(Res.string.submit_url_batch_progress, completedCount, totalCount),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
            modifier = Modifier.weight(1f),
        )
        if (isBatchSubmitting) {
            BracketButton(
                label = stringResource(Res.string.submit_url_batch_cancel),
                onClick = onCancel,
            )
        } else {
            BracketButton(
                label = stringResource(Res.string.submit_url_batch_submit_more),
                onClick = onReset,
            )
        }
    }
}

@Suppress("FunctionNaming", "LongMethod")
@Composable
private fun BatchUrlEntryRow(
    entry: BatchUrlEntry,
    onRetry: () -> Unit,
    onSkip: () -> Unit,
    onSubmitAnyway: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        // Status indicator
        when (entry.status) {
            BatchUrlStatus.PENDING -> {
                FrostIcon(
                    imageVector = AppIcons.CircleOutline,
                    contentDescription = stringResource(Res.string.submit_url_batch_pending),
                    tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.CHECKING -> {
                FrostSpinner(size = 16.dp)
            }
            BatchUrlStatus.SUBMITTING -> {
                FrostText(
                    text = stringResource(Res.string.common_percent, (entry.progress * 100).toInt()),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink,
                    modifier = Modifier.width(AppTheme.spacing.padPage),
                )
            }
            BatchUrlStatus.COMPLETED -> {
                FrostIcon(
                    imageVector = AppIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.submit_url_batch_completed),
                    tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active),
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.FAILED -> {
                FrostIcon(
                    imageVector = AppIcons.Close,
                    contentDescription = stringResource(Res.string.submit_url_batch_failed),
                    tint = AppTheme.frostColors.spark,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.SKIPPED -> {
                FrostIcon(
                    imageVector = AppIcons.Close,
                    contentDescription = stringResource(Res.string.submit_url_batch_skipped),
                    tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
        }

        // URL text
        Column(modifier = Modifier.weight(1f)) {
            FrostText(
                text = entry.url,
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val entryError = entry.error
            if (entry.status == BatchUrlStatus.FAILED && entryError != null) {
                FrostText(
                    text = entryError,
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (entry.isDuplicate && entry.status == BatchUrlStatus.SKIPPED) {
                FrostText(
                    text = stringResource(Res.string.submit_url_batch_duplicate_skipped),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                )
            }
        }

        // Action buttons
        when (entry.status) {
            BatchUrlStatus.FAILED -> {
                BracketButton(
                    label = stringResource(Res.string.submit_url_batch_retry),
                    onClick = onRetry,
                )
            }
            BatchUrlStatus.SKIPPED -> {
                if (entry.isDuplicate) {
                    BracketButton(
                        label = stringResource(Res.string.submit_url_batch_submit_anyway),
                        onClick = onSubmitAnyway,
                    )
                }
            }
            else -> {}
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SubmitURLHeader(onBackClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(AppTheme.frostColors.page)
                .padding(horizontal = AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BracketIconButton(
            onClick = onBackClick,
            contentDescription = stringResource(Res.string.submit_url_back),
        ) {
            FrostIcon(
                imageVector = AppIcons.ArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        FrostText(
            text = stringResource(Res.string.submit_url_title),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun URLInputSection(
    url: String,
    onUrlChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    error: String?,
    submitError: SubmitUrlError? = null,
    onViewLibrary: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        FrostText(
            text = stringResource(Res.string.submit_url_enter_prompt),
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )

        BracketField(
            value = url,
            onValueChange = onUrlChanged,
            label = stringResource(Res.string.submit_url_label),
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        when (submitError) {
            is SubmitUrlError.InvalidUrl -> {
                FrostText(
                    text = stringResource(Res.string.submit_url_error_invalid_url),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
                )
            }
            is SubmitUrlError.DuplicateUrl -> {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell)) {
                    FrostText(
                        text = stringResource(Res.string.submit_url_error_duplicate),
                        style = AppTheme.frostType.monoXs,
                        color = AppTheme.frostColors.spark,
                    )
                    BracketButton(
                        label = stringResource(Res.string.submit_url_error_view_library),
                        onClick = onViewLibrary,
                    )
                }
            }
            is SubmitUrlError.NetworkError -> {
                FrostText(
                    text = stringResource(Res.string.submit_url_error_network),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
                )
            }
            is SubmitUrlError.ServerError -> {
                FrostText(
                    text = stringResource(Res.string.submit_url_error_server),
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
                )
            }
            is SubmitUrlError.Unknown -> {
                FrostText(
                    text = submitError.message,
                    style = AppTheme.frostType.monoXs,
                    color = AppTheme.frostColors.spark,
                )
            }
            null -> {
                if (error != null) {
                    FrostText(
                        text = error,
                        style = AppTheme.frostType.monoXs,
                        color = AppTheme.frostColors.spark,
                    )
                }
            }
        }

        val submitLabel =
            if (isLoading) {
                stringResource(Res.string.submit_url_submitting)
            } else {
                stringResource(Res.string.submit_url_submit)
            }
        BracketButton(
            label = submitLabel,
            onClick = onSubmit,
            enabled = !isLoading && url.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DuplicateWarningSection(
    summaryId: String?,
    onViewExisting: (String?) -> Unit,
    onForceSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
        ) {
            FrostIcon(
                imageVector = AppIcons.WarningAlt,
                contentDescription = stringResource(Res.string.submit_url_duplicate_warning),
                tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                modifier = Modifier.size(IconSizes.md),
            )
            FrostText(
                text = stringResource(Res.string.submit_url_duplicate_title),
                style = AppTheme.frostType.monoEmph,
                color = AppTheme.frostColors.ink,
            )
        }

        FrostText(
            text = stringResource(Res.string.submit_url_duplicate_message),
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
        ) {
            if (summaryId != null) {
                BracketButton(
                    label = stringResource(Res.string.submit_url_view_existing),
                    onClick = { onViewExisting(summaryId) },
                    modifier = Modifier.weight(1f),
                )
            }

            BracketButton(
                label = stringResource(Res.string.submit_url_submit_anyway),
                onClick = onForceSubmit,
                modifier = Modifier.weight(1f),
            )

            BracketButton(
                label = stringResource(Res.string.submit_url_cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SubmissionProgressSection(state: SubmitURLState) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.line),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        FrostText(
            text = stringResource(Res.string.submit_url_processing),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
        )

        // note: Frost two-color rule — indeterminate progress rendered as ink hairline bar
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(AppTheme.frostColors.ink.copy(alpha = AppTheme.border.separatorAlpha)),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
        ) {
            FrostSpinner(size = 16.dp)
            FrostText(
                text = state.message ?: stringResource(Res.string.submit_url_processing_default_message),
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
        }

        FrostText(
            text =
                stringResource(
                    Res.string.submit_url_processing_stage,
                    processingStageLabel(state.stage),
                ),
            style = AppTheme.frostType.monoXs,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )
    }
}

@Composable
private fun processingStageLabel(stage: ProcessingStage): String =
    when (stage) {
        ProcessingStage.UNSPECIFIED -> stringResource(Res.string.submit_url_processing_default_message)
        ProcessingStage.QUEUED -> stringResource(Res.string.processing_stage_queued)
        ProcessingStage.EXTRACTION -> stringResource(Res.string.processing_stage_extraction)
        ProcessingStage.SUMMARIZATION -> stringResource(Res.string.processing_stage_summarization)
        ProcessingStage.SAVING -> stringResource(Res.string.processing_stage_saving)
        ProcessingStage.DONE -> stringResource(Res.string.processing_stage_done)
    }

@Suppress("FunctionNaming")
@Composable
private fun CompletionSection() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.line),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
    ) {
        FrostIcon(
            imageVector = AppIcons.CheckmarkFilled,
            contentDescription = stringResource(Res.string.submit_url_batch_completed),
            tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active),
            modifier = Modifier.size(IconSizes.md),
        )
        FrostText(
            text = stringResource(Res.string.submit_url_success),
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RequestHistoryHeader(
    showHistory: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FrostText(
            text = stringResource(Res.string.submit_url_request_history),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
            modifier = Modifier.weight(1f),
        )

        val historyDesc =
            if (showHistory) {
                stringResource(Res.string.submit_url_hide_history)
            } else {
                stringResource(Res.string.submit_url_show_history)
            }
        BracketIconButton(
            onClick = onToggle,
            contentDescription = historyDesc,
        ) {
            FrostIcon(
                imageVector = if (showHistory) AppIcons.ChevronUp else AppIcons.ChevronDown,
                contentDescription = null,
                modifier = Modifier.size(IconSizes.md),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RequestHistoryItem(
    request: Request,
    onRetry: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Status icon
        val statusIcon =
            when (request.status) {
                RequestStatus.COMPLETED -> AppIcons.CheckmarkFilled
                RequestStatus.FAILED -> AppIcons.Close
                RequestStatus.PROCESSING -> AppIcons.Renew
                RequestStatus.PENDING -> AppIcons.CircleOutline
            }
        val statusColor =
            when (request.status) {
                RequestStatus.COMPLETED -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active)
                RequestStatus.FAILED -> AppTheme.frostColors.spark
                RequestStatus.PROCESSING -> AppTheme.frostColors.ink
                RequestStatus.PENDING -> AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary)
            }

        FrostIcon(
            imageVector = statusIcon,
            contentDescription =
                when (request.status) {
                    RequestStatus.COMPLETED -> stringResource(Res.string.submit_url_request_status_completed)
                    RequestStatus.FAILED -> stringResource(Res.string.submit_url_request_status_failed)
                    RequestStatus.PROCESSING -> stringResource(Res.string.submit_url_request_status_processing)
                    RequestStatus.PENDING -> stringResource(Res.string.submit_url_request_status_pending)
                },
            tint = statusColor,
            modifier = Modifier.size(IconSizes.sm),
        )

        Spacer(modifier = Modifier.width(AppTheme.spacing.cell))

        // URL text (truncated)
        FrostText(
            text = request.url,
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )

        // Retry button for failed requests
        if (request.status == RequestStatus.FAILED) {
            BracketIconButton(
                onClick = onRetry,
                contentDescription = stringResource(Res.string.settings_retry),
            ) {
                FrostIcon(
                    imageVector = AppIcons.Renew,
                    contentDescription = null,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
        }
    }
}
