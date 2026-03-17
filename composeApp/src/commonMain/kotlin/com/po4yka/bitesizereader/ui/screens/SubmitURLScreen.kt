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
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing

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
                            text = "No recent requests",
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
            label = "Single",
            isSelected = !isBatchMode,
            onClick = { if (isBatchMode) onToggle() },
        )
        ModeChip(
            label = "Batch",
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
            text = "Paste URLs to summarize, one per line",
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
            label = "Submit All",
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
                label = "Cancel",
                onClick = onCancel,
                buttonType = ButtonType.Ghost,
            )
        } else {
            Button(
                label = "Submit More",
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
                    contentDescription = "Pending",
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
                    contentDescription = "Completed",
                    tint = Carbon.theme.supportSuccess,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.FAILED -> {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = "Failed",
                    tint = Carbon.theme.supportError,
                    modifier = Modifier.size(IconSizes.sm),
                )
            }
            BatchUrlStatus.SKIPPED -> {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = "Skipped",
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
                    text = "Duplicate — skipped",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }

        // Action buttons
        when (entry.status) {
            BatchUrlStatus.FAILED -> {
                Button(
                    label = "Retry",
                    onClick = onRetry,
                    buttonType = ButtonType.Ghost,
                )
            }
            BatchUrlStatus.SKIPPED -> {
                if (entry.isDuplicate) {
                    Button(
                        label = "Submit Anyway",
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
                contentDescription = "Back",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(IconSizes.md),
            )
        }

        Text(
            text = "Submit URL",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun URLInputSection(
    url: String,
    onUrlChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    error: String?,
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = "Enter an article or video URL to summarize",
            style = Carbon.typography.body01,
            color = Carbon.theme.textSecondary,
        )

        TextInput(
            label = "URL",
            value = url,
            onValueChange = onUrlChanged,
            state =
                when {
                    error != null -> TextInputState.Error
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

        if (error != null) {
            Text(
                text = error,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }

        Button(
            label = if (isLoading) "Submitting..." else "Submit",
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
                contentDescription = "Warning",
                tint = Carbon.theme.supportWarning,
                modifier = Modifier.size(IconSizes.md),
            )
            Text(
                text = "Duplicate URL Detected",
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
        }

        Text(
            text = "This URL has already been summarized. You can view the existing summary or submit it again.",
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (summaryId != null) {
                Button(
                    label = "View Existing",
                    onClick = { onViewExisting(summaryId) },
                    buttonType = ButtonType.Primary,
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                label = "Submit Anyway",
                onClick = onForceSubmit,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.weight(1f),
            )

            Button(
                label = "Cancel",
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
            text = "Processing",
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
            text = "Summary created successfully!",
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
            text = "Request History",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (showHistory) CarbonIcons.ChevronUp else CarbonIcons.ChevronDown,
                contentDescription = if (showHistory) "Hide history" else "Show history",
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
