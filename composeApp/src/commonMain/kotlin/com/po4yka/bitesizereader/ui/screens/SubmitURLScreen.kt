package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.ProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel

/**
 * Submit URL screen with validation and progress tracking using Carbon Design System
 */
@Composable
fun SubmitURLScreen(
    viewModel: SubmitURLViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Carbon.theme.background)
    ) {
        // Header
        CarbonSubmitHeader(
            title = "Submit URL",
            onBackClick = onBackClick,
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            URLInputForm(
                url = state.url,
                onUrlChange = { viewModel.onUrlChanged(it) },
                validationError = state.error,
                onSubmit = { viewModel.submitUrl() },
                isSubmitting = state.isLoading,
            )

            if (state.isLoading || state.status != RequestStatus.PENDING) {
                ProcessingStatus(
                    status = state.status,
                    stage = state.stage,
                    message = state.message,
                    progress = state.progress,
                    onCancel = { /* no-op for now */ },
                    onSubmitAnother = { viewModel.onUrlChanged("") },
                )
            }
        }
    }
}

@Composable
private fun CarbonSubmitHeader(
    title: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Carbon.theme.layer01)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Carbon.theme.iconPrimary,
            )
        }

        Text(
            text = title,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
        )
    }
}

@Composable
private fun URLInputForm(
    url: String,
    onUrlChange: (String) -> Unit,
    validationError: String?,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Enter the URL of an article you want to summarize",
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textPrimary,
        )

        TextInput(
            label = "Article URL",
            value = url,
            onValueChange = onUrlChange,
            placeholderText = "https://example.com/article",
            state = if (validationError != null) TextInputState.Error else if (isSubmitting) TextInputState.Disabled else TextInputState.Enabled,
            helperText = validationError ?: "",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    if (url.isNotBlank() && !isSubmitting) {
                        onSubmit()
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        if (isSubmitting) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                SmallLoading()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Submitting...",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
            }
        } else {
            Button(
                label = "Submit URL",
                onClick = onSubmit,
                isEnabled = url.isNotBlank() && !isSubmitting,
                buttonType = ButtonType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // How it works card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "How it works",
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )
            Text(
                text = "1. Enter a URL to any web article\n" +
                    "2. Our AI will download and analyze the content\n" +
                    "3. Get a concise summary with key ideas\n" +
                    "4. Processing typically takes 30-60 seconds",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}

@Composable
private fun ProcessingStatus(
    status: RequestStatus,
    stage: ProcessingStage,
    message: String?,
    progress: Float,
    onCancel: () -> Unit,
    onSubmitAnother: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        val title = when (status) {
            RequestStatus.COMPLETED -> "Summary Ready!"
            RequestStatus.FAILED -> "Processing Failed"
            else -> "Processing Your Article"
        }

        val titleColor = when (status) {
            RequestStatus.COMPLETED -> Carbon.theme.supportSuccess
            RequestStatus.FAILED -> Carbon.theme.supportError
            else -> Carbon.theme.textPrimary
        }

        Text(
            text = title,
            style = Carbon.typography.heading03,
            color = titleColor,
        )

        // Show detailed status if running or failed
        if (status != RequestStatus.COMPLETED) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgressBar(
                    value = progress,
                    modifier = Modifier.fillMaxWidth(),
                    state = when (status) {
                        RequestStatus.FAILED -> ProgressBarState.Error
                        RequestStatus.COMPLETED -> ProgressBarState.Success
                        else -> ProgressBarState.Active
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stage.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
                    )
                }

                if (!message.isNullOrBlank()) {
                    Text(
                        text = message,
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }
        } else {
            ProgressBar(
                value = 1f,
                modifier = Modifier.fillMaxWidth(),
                state = ProgressBarState.Success,
            )
            Text(
                text = "Processing Complete",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        when (status) {
            RequestStatus.COMPLETED -> {
                Button(
                    label = "Submit Another URL",
                    onClick = onSubmitAnother,
                    buttonType = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            RequestStatus.FAILED -> {
                Button(
                    label = "Try Again",
                    onClick = onSubmitAnother,
                    buttonType = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            RequestStatus.PROCESSING, RequestStatus.PENDING -> {
                Button(
                    label = "Cancel",
                    onClick = onCancel,
                    buttonType = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            else -> {}
        }
    }
}
