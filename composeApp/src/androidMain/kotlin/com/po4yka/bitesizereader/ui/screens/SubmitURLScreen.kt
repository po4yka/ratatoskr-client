package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    onSuccess: (Int) -> Unit,
    modifier: Modifier = Modifier
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
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.requestStatus == null) {
                // URL Input Form
                URLInputForm(
                    url = state.url,
                    onUrlChange = { viewModel.onUrlChange(it) },
                    validationError = state.validationError,
                    onSubmit = { viewModel.submitUrl() },
                    isSubmitting = state.isSubmitting
                )
            } else {
                // Processing Status
                ProcessingStatus(
                    status = state.requestStatus,
                    onCancel = { viewModel.cancelRequest() },
                    onViewSummary = state.summaryId?.let { id ->
                        { onSuccess(id) }
                    },
                    onSubmitAnother = { viewModel.reset() }
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
    isSubmitting: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter the URL of an article you want to summarize",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Article URL") },
            placeholder = { Text("https://example.com/article") },
            isError = validationError != null,
            supportingText = validationError?.let {
                { Text(it) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        )

        Button(
            onClick = onSubmit,
            enabled = url.isNotBlank() && !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isSubmitting) "Submitting..." else "Submit URL")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "How it works",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "1. Enter a URL to any web article\n" +
                            "2. Our AI will download and analyze the content\n" +
                            "3. Get a concise summary with key ideas\n" +
                            "4. Processing typically takes 30-60 seconds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProcessingStatus(
    status: RequestStatus?,
    onCancel: () -> Unit,
    onViewSummary: (() -> Unit)?,
    onSubmitAnother: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        when (status) {
            RequestStatus.COMPLETED -> {
                Text(
                    text = "Summary Ready!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            RequestStatus.FAILED, RequestStatus.CANCELLED -> {
                Text(
                    text = if (status == RequestStatus.CANCELLED) "Processing Cancelled" else "Processing Failed",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                Text(
                    text = "Processing Your Article",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        status?.let {
            ProgressIndicatorWithStages(status = it)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        when (status) {
            RequestStatus.COMPLETED -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    onViewSummary?.let { onClick ->
                        Button(
                            onClick = onClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Summary")
                        }
                    }
                    OutlinedButton(
                        onClick = onSubmitAnother,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Another URL")
                    }
                }
            }
            RequestStatus.FAILED, RequestStatus.CANCELLED -> {
                OutlinedButton(
                    onClick = onSubmitAnother,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Try Again")
                }
            }
            else -> {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
