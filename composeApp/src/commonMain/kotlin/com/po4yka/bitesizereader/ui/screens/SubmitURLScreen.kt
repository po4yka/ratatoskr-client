package com.po4yka.bitesizereader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.IndeterminateProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
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
        modifier = modifier
            .fillMaxSize()
            .background(Carbon.theme.background),
    ) {
        // Header with back button
        SubmitURLHeader(
            onBackClick = component::onBackClicked,
        )

        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // URL Input section
            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
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
                            modifier = Modifier
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
private fun SubmitURLHeader(
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
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
            state = when {
                error != null -> TextInputState.Error
                isLoading -> TextInputState.Disabled
                else -> TextInputState.Enabled
            },
            placeholderText = "https://example.com/article",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
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
        modifier = Modifier
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
private fun SubmissionProgressSection(
    state: SubmitURLState,
) {
    Column(
        modifier = Modifier
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
        modifier = Modifier
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
        modifier = Modifier
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
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon.theme.layer01)
            .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Status icon
        val statusIcon = when (request.status) {
            RequestStatus.COMPLETED -> CarbonIcons.CheckmarkFilled
            RequestStatus.FAILED -> CarbonIcons.Close
            RequestStatus.PROCESSING -> CarbonIcons.Renew
            RequestStatus.PENDING -> CarbonIcons.CircleOutline
        }
        val statusColor = when (request.status) {
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
