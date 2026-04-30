package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.IngestLine
import com.po4yka.ratatoskr.core.ui.components.frost.IngestState
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadge
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadgeSeverity
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.RequestStatus
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.progress_stages_processing_summary
import ratatoskr.core.ui.generated.resources.progress_stages_ready
import ratatoskr.core.ui.generated.resources.progress_stages_submitted

/**
 * Progress indicator showing request processing stages as Frost status badges with IngestLine
 * connectors. Replaces M3 LinearProgressIndicator.
 */
@Suppress("FunctionNaming", "unused") // Composable naming convention; Public API
@Composable
fun ProgressIndicatorWithStages(
    status: RequestStatus,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Overall ingest line indicator
        IngestLine(
            state =
                when (status) {
                    RequestStatus.PENDING, RequestStatus.PROCESSING -> IngestState.Active
                    RequestStatus.COMPLETED -> IngestState.Idle
                    RequestStatus.FAILED -> IngestState.Error
                },
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md),
        )

        // Stage indicators as StatusBadge rows
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FrostStageRow(
                title = stringResource(Res.string.progress_stages_submitted),
                isCompleted = true,
                isCurrent = status == RequestStatus.PENDING,
                isFailed = false,
            )
            FrostStageRow(
                title = stringResource(Res.string.progress_stages_processing_summary),
                isCompleted = status == RequestStatus.COMPLETED || status == RequestStatus.FAILED,
                isCurrent = status == RequestStatus.PROCESSING,
                isFailed = status == RequestStatus.FAILED,
            )
            FrostStageRow(
                title = stringResource(Res.string.progress_stages_ready),
                isCompleted = status == RequestStatus.COMPLETED,
                isCurrent = false,
                isFailed = status == RequestStatus.FAILED,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun FrostStageRow(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isFailed: Boolean,
) {
    val ink = AppTheme.frostColors.ink
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        when {
            isFailed -> StatusBadge(label = "✗", severity = StatusBadgeSeverity.Alarm)
            isCompleted -> StatusBadge(label = "✓", severity = StatusBadgeSeverity.Info)
            isCurrent -> AppSmallSpinner()
            else -> StatusBadge(label = "·", severity = StatusBadgeSeverity.Info)
        }
        FrostText(
            text = title,
            style = AppTheme.frostType.monoBody,
            color =
                when {
                    isFailed -> ink
                    isCompleted || isCurrent -> ink
                    else -> ink.copy(alpha = AppTheme.alpha.secondary)
                },
        )
    }
}
