package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.ui.theme.FailedIndicator
import com.po4yka.bitesizereader.ui.theme.ProcessingIndicator

/**
 * Progress indicator showing request processing stages
 */
@Composable
fun ProgressIndicatorWithStages(
    status: RequestStatus,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Overall progress bar
        when (status) {
            RequestStatus.PENDING, RequestStatus.DOWNLOADING, RequestStatus.PROCESSING -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = ProcessingIndicator,
                )
            }
            RequestStatus.COMPLETED -> {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            RequestStatus.FAILED, RequestStatus.CANCELLED -> {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth(),
                    color = FailedIndicator,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stage indicators
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StageIndicator(
                title = "Submitted",
                isCompleted = true,
                isCurrent = status == RequestStatus.PENDING,
                isFailed = false,
            )

            StageIndicator(
                title = "Downloading Content",
                isCompleted = status in listOf(RequestStatus.PROCESSING, RequestStatus.COMPLETED),
                isCurrent = status == RequestStatus.DOWNLOADING,
                isFailed = status == RequestStatus.FAILED && status == RequestStatus.DOWNLOADING,
            )

            StageIndicator(
                title = "Processing Summary",
                isCompleted = status == RequestStatus.COMPLETED,
                isCurrent = status == RequestStatus.PROCESSING,
                isFailed = status == RequestStatus.FAILED && status == RequestStatus.PROCESSING,
            )

            StageIndicator(
                title = "Ready",
                isCompleted = status == RequestStatus.COMPLETED,
                isCurrent = false,
                isFailed = status in listOf(RequestStatus.FAILED, RequestStatus.CANCELLED),
            )
        }
    }
}

@Composable
private fun StageIndicator(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isFailed: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Stage icon
        when {
            isFailed -> {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = FailedIndicator,
                    modifier = Modifier.size(24.dp),
                )
            }
            isCompleted -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            isCurrent -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
            }
            else -> {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {}
            }
        }

        // Stage title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color =
                when {
                    isFailed -> FailedIndicator
                    isCompleted || isCurrent -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
    }
}
