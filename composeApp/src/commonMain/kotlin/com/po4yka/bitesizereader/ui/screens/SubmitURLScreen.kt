package com.po4yka.bitesizereader.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.IndeterminateProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.domain.model.BatchUrlEntry
import com.po4yka.bitesizereader.domain.model.BatchUrlStatus
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.presentation.navigation.SubmitURLComponent
import com.po4yka.bitesizereader.presentation.state.SubmitURLState
import com.po4yka.bitesizereader.presentation.state.SubmitUrlError
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.submit_url_back
import bitesizereader.composeapp.generated.resources.submit_url_batch_cancel
import bitesizereader.composeapp.generated.resources.submit_url_batch_completed
import bitesizereader.composeapp.generated.resources.submit_url_batch_duplicate_skipped
import bitesizereader.composeapp.generated.resources.submit_url_batch_failed
import bitesizereader.composeapp.generated.resources.submit_url_batch_pending
import bitesizereader.composeapp.generated.resources.submit_url_batch_prompt
import bitesizereader.composeapp.generated.resources.submit_url_batch_retry
import bitesizereader.composeapp.generated.resources.submit_url_batch_skipped
import bitesizereader.composeapp.generated.resources.submit_url_batch_submit_all
import bitesizereader.composeapp.generated.resources.submit_url_batch_submit_anyway
import bitesizereader.composeapp.generated.resources.submit_url_batch_submit_more
import bitesizereader.composeapp.generated.resources.submit_url_cancel
import bitesizereader.composeapp.generated.resources.submit_url_duplicate_message
import bitesizereader.composeapp.generated.resources.submit_url_duplicate_title
import bitesizereader.composeapp.generated.resources.submit_url_duplicate_warning
import bitesizereader.composeapp.generated.resources.submit_url_enter_prompt
import bitesizereader.composeapp.generated.resources.submit_url_hide_history
import bitesizereader.composeapp.generated.resources.submit_url_label
import bitesizereader.composeapp.generated.resources.submit_url_mode_batch
import bitesizereader.composeapp.generated.resources.submit_url_mode_single
import bitesizereader.composeapp.generated.resources.submit_url_no_recent_requests
import bitesizereader.composeapp.generated.resources.submit_url_processing
import bitesizereader.composeapp.generated.resources.submit_url_request_history
import bitesizereader.composeapp.generated.resources.submit_url_show_history
import bitesizereader.composeapp.generated.resources.submit_url_submit
import bitesizereader.composeapp.generated.resources.submit_url_submit_anyway
import bitesizereader.composeapp.generated.resources.submit_url_submitting
import bitesizereader.composeapp.generated.resources.submit_url_success
import bitesizereader.composeapp.generated.resources.submit_url_title
import bitesizereader.composeapp.generated.resources.submit_url_view_existing
import bitesizereader.composeapp.generated.resources.submit_url_error_invalid_url
import bitesizereader.composeapp.generated.resources.submit_url_error_duplicate
import bitesizereader.composeapp.generated.resources.submit_url_error_network
import bitesizereader.composeapp.generated.resources.submit_url_error_server
import bitesizereader.composeapp.generated.resources.submit_url_error_view_library
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming", "LongMethod")
@Composable
fun SubmitURLScreen(
    component: SubmitURLComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel: SubmitURLViewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
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
                    .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Mode toggle
            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
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
                Spacer(modifier = Modifier.height(Spacing.md))
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
                                    .padding(Spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            SmallLoading()
                        }
                    }
                } else if (state.recentRequests.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.submit_url_no_recent_requests),
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                            modifier = Modifier.padding(vertical = Spacing.md),
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
                Spacer(modifier = Modifier.height(Spacing.xl))
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
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
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
    val shape = RoundedCornerShape(Spacing.md)
    val backgroundColor = if (isSelected) Carbon.theme.backgroundInverse else Carbon.theme.layer01
    val textColor = if (isSelected) Carbon.theme.textOnColor else Carbon.theme.textSecondary
    val borderColor = if (isSelected) Carbon.theme.backgroundInverse else Carbon.theme.borderSubtle00

    Box(
        modifier =
            Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                .semantics {
                    role = Role.Tab
                    selected = isSelected
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = Carbon.typography.bodyCompact01,
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
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.submit_url_batch_prompt),
            style = Carbon.typography.body01,
            color = Carbon.theme.textSecondary,
        )

        OutlinedTextField(
            value = batchInput,
            onValueChange = onBatchInputChanged,
            modifier = Modifier.fillMaxWidth().height(180.dp),
            placeholder = {
                Text(
                    text = "https://example.com/article\nhttps://example.com/video",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Carbon.theme.borderSubtle00,
                    unfocusedBorderColor = Carbon.theme.borderSubtle00,
                    cursorColor = Carbon.theme.textPrimary,
                    focusedTextColor = Carbon.theme.textPrimary,
                    unfocusedTextColor = Carbon.theme.textPrimary,
                    focusedContainerColor = Carbon.theme.layer01,
                    unfocusedContainerColor = Carbon.theme.layer01,
                ),
            textStyle = Carbon.typography.bodyCompact01,
        )

        Text(
            text = "$urlCount URL${if (urlCount == 1) "" else "s"} detected",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )

        Button(
            label = stringResource(Res.string.submit_url_batch_submit_all),
            onClick = onSubmitBatch,
            isEnabled = urlCount > 0,
            buttonType = ButtonType.Primary,
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
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$completedCount of $totalCount completed",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (isBatchSubmitting) {
            Button(
                label = stringResource(Res.string.submit_url_batch_cancel),
                onClick = onCancel,
                buttonType = ButtonType.Ghost,
            )
        } else {
            Button(
                label = stringResource(Res.string.submit_url_batch_submit_more),
                onClick = onReset,
                buttonType = ButtonType.Ghost,
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
                .background(Carbon.theme.layer01)
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // Status indicator
        when (entry.status) {
            BatchUrlStatus.PENDING -> {
                Icon(
                    imageVector = CarbonIcons.CircleOutline,
                    contentDescription = stringResource(Res.string.submit_url_batch_pending),
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.CHECKING -> {
                SmallLoading()
            }
            BatchUrlStatus.SUBMITTING -> {
                Text(
                    text = "${(entry.progress * 100).toInt()}%",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.linkPrimary,
                    modifier = Modifier.width(Spacing.xl),
                )
            }
            BatchUrlStatus.COMPLETED -> {
                Icon(
                    imageVector = CarbonIcons.CheckmarkFilled,
                    contentDescription = stringResource(Res.string.submit_url_batch_completed),
                    tint = Carbon.theme.supportSuccess,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.FAILED -> {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = stringResource(Res.string.submit_url_batch_failed),
                    tint = Carbon.theme.supportError,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.SKIPPED -> {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = stringResource(Res.string.submit_url_batch_skipped),
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
        }

        // URL text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.url,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val entryError = entry.error
            if (entry.status == BatchUrlStatus.FAILED && entryError != null) {
                Text(
                    text = entryError,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (entry.isDuplicate && entry.status == BatchUrlStatus.SKIPPED) {
                Text(
                    text = stringResource(Res.string.submit_url_batch_duplicate_skipped),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }

        // Action buttons
        when (entry.status) {
            BatchUrlStatus.FAILED -> {
                Button(
                    label = stringResource(Res.string.submit_url_batch_retry),
                    onClick = onRetry,
                    buttonType = ButtonType.Ghost,
                )
            }
            BatchUrlStatus.SKIPPED -> {
                if (entry.isDuplicate) {
                    Button(
                        label = stringResource(Res.string.submit_url_batch_submit_anyway),
                        onClick = onSubmitAnyway,
                        buttonType = ButtonType.Ghost,
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
                .height(Dimensions.detailHeaderHeight)
                .background(Carbon.theme.layer01)
                .padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = CarbonIcons.ArrowLeft,
                contentDescription = stringResource(Res.string.submit_url_back),
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        Text(
            text = stringResource(Res.string.submit_url_title),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
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
    val focusManager = LocalFocusManager.current
    val hasError = error != null || submitError != null

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.submit_url_enter_prompt),
            style = Carbon.typography.body01,
            color = Carbon.theme.textSecondary,
        )

        TextInput(
            label = stringResource(Res.string.submit_url_label),
            value = url,
            onValueChange = onUrlChanged,
            state =
                when {
                    hasError -> TextInputState.Error
                    isLoading -> TextInputState.Disabled
                    else -> TextInputState.Enabled
                },
            placeholderText = "https://example.com/article",
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSubmit()
                    },
                ),
            modifier = Modifier.fillMaxWidth(),
        )

        when (submitError) {
            is SubmitUrlError.InvalidUrl -> {
                Text(
                    text = stringResource(Res.string.submit_url_error_invalid_url),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
            is SubmitUrlError.DuplicateUrl -> {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = stringResource(Res.string.submit_url_error_duplicate),
                        style = Carbon.typography.label01,
                        color = Carbon.theme.supportError,
                    )
                    Button(
                        label = stringResource(Res.string.submit_url_error_view_library),
                        onClick = onViewLibrary,
                        buttonType = ButtonType.Ghost,
                    )
                }
            }
            is SubmitUrlError.NetworkError -> {
                Text(
                    text = stringResource(Res.string.submit_url_error_network),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
            is SubmitUrlError.ServerError -> {
                Text(
                    text = stringResource(Res.string.submit_url_error_server),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
            is SubmitUrlError.Unknown -> {
                Text(
                    text = submitError.message,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }
            null -> {
                if (error != null) {
                    Text(
                        text = error,
                        style = Carbon.typography.label01,
                        color = Carbon.theme.supportError,
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
        Button(
            label = submitLabel,
            onClick = onSubmit,
            isEnabled = !isLoading && url.isNotBlank(),
            buttonType = ButtonType.Primary,
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
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                imageVector = CarbonIcons.WarningAlt,
                contentDescription = stringResource(Res.string.submit_url_duplicate_warning),
                tint = Carbon.theme.supportWarning,
                modifier = Modifier.size(IconSizes.md),
            )
            Text(
                text = stringResource(Res.string.submit_url_duplicate_title),
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
        }

        Text(
            text = stringResource(Res.string.submit_url_duplicate_message),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (summaryId != null) {
                Button(
                    label = stringResource(Res.string.submit_url_view_existing),
                    onClick = { onViewExisting(summaryId) },
                    buttonType = ButtonType.Primary,
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                label = stringResource(Res.string.submit_url_submit_anyway),
                onClick = onForceSubmit,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.weight(1f),
            )

            Button(
                label = stringResource(Res.string.submit_url_cancel),
                onClick = onDismiss,
                buttonType = ButtonType.Ghost,
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
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.submit_url_processing),
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        IndeterminateProgressBar(
            modifier = Modifier.fillMaxWidth(),
            state = ProgressBarState.Active,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            SmallLoading()
            Text(
                text = state.message ?: "Processing...",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }

        Text(
            text = "Stage: ${state.stage.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CompletionSection() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = CarbonIcons.CheckmarkFilled,
            contentDescription = "Completed",
            tint = Carbon.theme.supportSuccess,
            modifier = Modifier.size(IconSizes.md),
        )
        Text(
            text = stringResource(Res.string.submit_url_success),
            style = Carbon.typography.body01,
            color = Carbon.theme.supportSuccess,
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
        Text(
            text = stringResource(Res.string.submit_url_request_history),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        val historyDesc =
            if (showHistory) {
                stringResource(Res.string.submit_url_hide_history)
            } else {
                stringResource(Res.string.submit_url_show_history)
            }
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (showHistory) CarbonIcons.ChevronUp else CarbonIcons.ChevronDown,
                contentDescription = historyDesc,
                tint = Carbon.theme.iconPrimary,
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
                .background(Carbon.theme.layer01)
                .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Status icon
        val statusIcon =
            when (request.status) {
                RequestStatus.COMPLETED -> CarbonIcons.CheckmarkFilled
                RequestStatus.FAILED -> CarbonIcons.Close
                RequestStatus.PROCESSING -> CarbonIcons.Renew
                RequestStatus.PENDING -> CarbonIcons.CircleOutline
            }
        val statusColor =
            when (request.status) {
                RequestStatus.COMPLETED -> Carbon.theme.supportSuccess
                RequestStatus.FAILED -> Carbon.theme.supportError
                RequestStatus.PROCESSING -> Carbon.theme.linkPrimary
                RequestStatus.PENDING -> Carbon.theme.iconSecondary
            }

        Icon(
            imageVector = statusIcon,
            contentDescription = request.status.name,
            tint = statusColor,
            modifier = Modifier.size(IconSizes.sm),
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        // URL text (truncated)
        Text(
            text = request.url,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textPrimary,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )

        // Retry button for failed requests
        if (request.status == RequestStatus.FAILED) {
            IconButton(onClick = onRetry) {
                Icon(
                    imageVector = CarbonIcons.Renew,
                    contentDescription = "Retry",
                    tint = Carbon.theme.iconPrimary,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
        }
    }
}
