package com.po4yka.ratatoskr.core.ui.components

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
import androidx.compose.material3.LinearProgressIndicator
import com.po4yka.ratatoskr.core.ui.icons.CarbonIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.progress_stages_completed
import ratatoskr.core.ui.generated.resources.progress_stages_failed
import ratatoskr.core.ui.generated.resources.progress_stages_processing_summary
import ratatoskr.core.ui.generated.resources.progress_stages_ready
import ratatoskr.core.ui.generated.resources.progress_stages_submitted
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.RequestStatus
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

/**
 * Progress indicator showing request processing stages.
 *
 * Material 3 [LinearProgressIndicator] backs the bar. The semantic states (Active / Success / Error)
 * that Carbon's `ProgressBarState` carried are expressed by the choice of color and progress value
 * at each call site.
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
        // Overall progress bar
        when (status) {
            RequestStatus.PENDING, RequestStatus.PROCESSING -> {
                // Indeterminate: omit `progress` argument.
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            RequestStatus.COMPLETED -> {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth(),
                    color = AppTheme.colors.supportSuccess,
                )
            }
            RequestStatus.FAILED -> {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth(),
                    color = AppTheme.colors.supportError,
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
                    tint = AppTheme.colors.supportError,
                    modifier = Modifier.size(Dimensions.stageIndicatorSize),
                )
            }
            isCompleted -> {
                Icon(
                    imageVector = CarbonIcons.Checkmark,
                    contentDescription = stringResource(Res.string.progress_stages_completed, title),
                    tint = AppTheme.colors.supportSuccess,
                    modifier = Modifier.size(Dimensions.stageIndicatorSize),
                )
            }
            isCurrent -> {
                AppSmallSpinner(
                    modifier = Modifier.size(Dimensions.stageIndicatorSize),
                )
            }
            else -> {
                Box(
                    modifier =
                        Modifier
                            .size(Dimensions.stageIndicatorSize)
                            .background(AppTheme.colors.layer02, shape = androidx.compose.foundation.shape.CircleShape),
                )
            }
        }

        // Stage title
        Text(
            text = title,
            style = AppTheme.type.bodyCompact01,
            color =
                when {
                    isFailed -> AppTheme.colors.supportError
                    isCompleted || isCurrent -> AppTheme.colors.textPrimary
                    else -> AppTheme.colors.textSecondary
                },
        )
    }
}
