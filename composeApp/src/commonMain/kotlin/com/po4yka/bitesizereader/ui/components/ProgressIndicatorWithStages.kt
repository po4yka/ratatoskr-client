package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.IndeterminateProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.ui.theme.FailedIndicator
import com.po4yka.bitesizereader.ui.theme.ProcessingIndicator

/**
 * Progress indicator showing request processing stages using Carbon Design System
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
        // Overall progress bar using Carbon ProgressBar
        when (status) {
            RequestStatus.PENDING, RequestStatus.PROCESSING -> {
                IndeterminateProgressBar(
                    modifier = Modifier.fillMaxWidth(),
                    state = ProgressBarState.Active,
                )
            }
            RequestStatus.COMPLETED -> {
                ProgressBar(
                    value = 1f,
                    modifier = Modifier.fillMaxWidth(),
                    state = ProgressBarState.Success,
                )
            }
            RequestStatus.FAILED -> {
                ProgressBar(
                    value = 1f,
                    modifier = Modifier.fillMaxWidth(),
                    state = ProgressBarState.Error,
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
                title = "Processing Summary",
                isCompleted = status == RequestStatus.COMPLETED,
                isCurrent = status == RequestStatus.PROCESSING,
                isFailed = status == RequestStatus.FAILED && status == RequestStatus.PROCESSING,
            )

            StageIndicator(
                title = "Ready",
                isCompleted = status == RequestStatus.COMPLETED,
                isCurrent = false,
                isFailed = status == RequestStatus.FAILED,
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
                    imageVector = CarbonIcons.Close,
                    contentDescription = null,
                    tint = Carbon.theme.supportError,
                    modifier = Modifier.size(24.dp),
                )
            }
            isCompleted -> {
                Icon(
                    imageVector = CarbonIcons.Checkmark,
                    contentDescription = null,
                    tint = Carbon.theme.supportSuccess,
                    modifier = Modifier.size(24.dp),
                )
            }
            isCurrent -> {
                SmallLoading(
                    modifier = Modifier.size(24.dp),
                )
            }
            else -> {
                Surface(
                    modifier = Modifier.size(24.dp),
                    color = Carbon.theme.layer02,
                ) {}
            }
        }

        // Stage title
        Text(
            text = title,
            style = Carbon.typography.bodyCompact01,
            color =
                when {
                    isFailed -> Carbon.theme.supportError
                    isCompleted || isCurrent -> Carbon.theme.textPrimary
                    else -> Carbon.theme.textSecondary
                },
        )
    }
}
