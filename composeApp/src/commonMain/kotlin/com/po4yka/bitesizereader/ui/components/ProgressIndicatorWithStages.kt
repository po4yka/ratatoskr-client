package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.progress_stages_completed
import bitesizereader.composeapp.generated.resources.progress_stages_failed
import bitesizereader.composeapp.generated.resources.progress_stages_processing_summary
import bitesizereader.composeapp.generated.resources.progress_stages_ready
import bitesizereader.composeapp.generated.resources.progress_stages_submitted
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.progressbar.IndeterminateProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBar
import com.gabrieldrn.carbon.progressbar.ProgressBarState
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

/**
 * Progress indicator showing request processing stages using Carbon Design System
 */
@Suppress("FunctionNaming", "unused") // Composable naming convention; Public API
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

        Spacer(modifier = Modifier.height(Spacing.md))

        // Stage indicators
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            StageIndicator(
                title = stringResource(Res.string.progress_stages_submitted),
                isCompleted = true,
                isCurrent = status == RequestStatus.PENDING,
                isFailed = false,
            )

            StageIndicator(
                title = stringResource(Res.string.progress_stages_processing_summary),
                isCompleted = status == RequestStatus.COMPLETED || status == RequestStatus.FAILED,
                isCurrent = status == RequestStatus.PROCESSING,
                isFailed = status == RequestStatus.FAILED,
            )

            StageIndicator(
                title = stringResource(Res.string.progress_stages_ready),
                isCompleted = status == RequestStatus.COMPLETED,
                isCurrent = false,
                isFailed = status == RequestStatus.FAILED,
            )
        }
    }
}

@Suppress("FunctionNaming") // Composable naming convention
@Composable
private fun StageIndicator(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isFailed: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // Stage icon
        when {
            isFailed -> {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = stringResource(Res.string.progress_stages_failed, title),
                    tint = Carbon.theme.supportError,
                    modifier = Modifier.size(Dimensions.stageIndicatorSize),
                )
            }
            isCompleted -> {
                Icon(
                    imageVector = CarbonIcons.Checkmark,
                    contentDescription = stringResource(Res.string.progress_stages_completed, title),
                    tint = Carbon.theme.supportSuccess,
                    modifier = Modifier.size(Dimensions.stageIndicatorSize),
                )
            }
            isCurrent -> {
                SmallLoading(
                    modifier = Modifier.size(Dimensions.stageIndicatorSize),
                )
            }
            else -> {
                Box(
                    modifier =
                        Modifier
                            .size(Dimensions.stageIndicatorSize)
                            .background(Carbon.theme.layer02, shape = androidx.compose.foundation.shape.CircleShape),
                )
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
