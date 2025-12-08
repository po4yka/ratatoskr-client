package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.ui.components.ProgressIndicatorWithStages

/**
 * Submit URL screen with validation and progress tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitURLScreen(
    viewModel: SubmitURLViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit URL") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
            style = MaterialTheme.typography.bodyLarge,
        )

        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Article URL") },
            placeholder = { Text("https://example.com/article") },
            isError = validationError != null,
            supportingText =
                validationError?.let {
                    { Text(it) }
                },
            trailingIcon = {
                if (url.isNotEmpty() && !isSubmitting) {
                    IconButton(onClick = { onUrlChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
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
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        )

        Button(
            onClick = onSubmit,
            enabled = url.isNotBlank() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isSubmitting) "Submitting..." else "Submit URL")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "How it works",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text =
                        "1. Enter a URL to any web article\n" +
                            "2. Our AI will download and analyze the content\n" +
                            "3. Get a concise summary with key ideas\n" +
                            "4. Processing typically takes 30-60 seconds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
             RequestStatus.COMPLETED -> MaterialTheme.colorScheme.primary
             RequestStatus.FAILED -> MaterialTheme.colorScheme.error
             else -> MaterialTheme.colorScheme.onSurface
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = titleColor,
        )

        // Show detailed status if running or failed
        if (status != RequestStatus.COMPLETED) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stage.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (!message.isNullOrBlank()) {
                     Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
             // Completed state - simpler progress or success indicator
             LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
             Text(
                text = "Processing Complete",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        when (status) {
            RequestStatus.COMPLETED -> {
                OutlinedButton(
                    onClick = onSubmitAnother,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Submit Another URL")
                }
            }
            RequestStatus.FAILED -> {
                OutlinedButton(
                    onClick = onSubmitAnother,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Try Again")
                }
            }
            RequestStatus.PROCESSING, RequestStatus.PENDING -> {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true // TODO: Implement cancel logic in VM
                ) {
                    Text("Cancel")
                }
            }
            else -> {}
        }
    }
}
