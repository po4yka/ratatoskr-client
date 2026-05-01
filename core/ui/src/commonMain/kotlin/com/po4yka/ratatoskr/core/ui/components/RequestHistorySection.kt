@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadge
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadgeSeverity
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.RequestStatus
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.request_history_collapse
import ratatoskr.core.ui.generated.resources.request_history_count_plural
import ratatoskr.core.ui.generated.resources.request_history_count_singular
import ratatoskr.core.ui.generated.resources.request_history_empty
import ratatoskr.core.ui.generated.resources.request_history_expand
import ratatoskr.core.ui.generated.resources.request_history_loading
import ratatoskr.core.ui.generated.resources.request_history_prompt
import ratatoskr.core.ui.generated.resources.request_history_retry
import ratatoskr.core.ui.generated.resources.request_history_status_completed
import ratatoskr.core.ui.generated.resources.request_history_status_failed
import ratatoskr.core.ui.generated.resources.request_history_status_pending
import ratatoskr.core.ui.generated.resources.request_history_status_processing
import ratatoskr.core.ui.generated.resources.submit_url_request_history

/**
 * Expandable section showing recent URL submission requests with their status.
 */
@Suppress("FunctionNaming")
@Composable
fun RequestHistorySection(
    requests: List<Request>,
    isLoading: Boolean,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onRetryRequest: (Request) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    BrutalistCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header (clickable to expand/collapse)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggleExpanded)
                        .padding(Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeading(text = stringResource(Res.string.submit_url_request_history))
                    FrostText(
                        text =
                            if (requests.isNotEmpty()) {
                                if (requests.size == 1) {
                                    stringResource(Res.string.request_history_count_singular, requests.size)
                                } else {
                                    stringResource(Res.string.request_history_count_plural, requests.size)
                                }
                            } else {
                                stringResource(Res.string.request_history_prompt)
                            },
                        style = AppTheme.frostType.monoSm,
                        color = ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }

                FrostIcon(
                    imageVector = AppIcons.ArrowLeft,
                    contentDescription =
                        if (isExpanded) {
                            stringResource(Res.string.request_history_collapse)
                        } else {
                            stringResource(Res.string.request_history_expand)
                        },
                    tint = ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier =
                        Modifier
                            .size(IconSizes.xs)
                            .graphicsLayer { rotationZ = if (isExpanded) 90f else 180f },
                )
            }

            // Request list (collapsible)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md)
                            .padding(bottom = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    when {
                        isLoading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AppSmallSpinner()
                                FrostText(
                                    text = stringResource(Res.string.request_history_loading),
                                    style = AppTheme.frostType.monoSm,
                                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                                    modifier = Modifier.padding(start = Spacing.xs),
                                )
                            }
                        }
                        requests.isEmpty() -> {
                            FrostText(
                                text = stringResource(Res.string.request_history_empty),
                                style = AppTheme.frostType.monoBody,
                                color = ink.copy(alpha = AppTheme.alpha.secondary),
                            )
                        }
                        else -> {
                            requests.take(10).forEach { request ->
                                RequestItem(
                                    request = request,
                                    onRetry = { onRetryRequest(request) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RequestItem(
    request: Request,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink
    val isFailed = request.status == RequestStatus.FAILED

    BrutalistCard(
        modifier = modifier.fillMaxWidth(),
        critical = isFailed,
        contentPadding = Spacing.sm,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                FrostText(
                    text = request.url,
                    style = AppTheme.frostType.monoBody,
                    color = ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RequestStatusBadge(status = request.status)
                    FrostText(
                        text = formatRequestDate(request.updatedAt.toString()),
                        style = AppTheme.frostType.monoSm,
                        color = ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }
            }

            if (isFailed) {
                AppIconButton(
                    imageVector = AppIcons.Renew,
                    contentDescription = stringResource(Res.string.request_history_retry),
                    onClick = onRetry,
                    buttonSize = Dimensions.compactIconButtonSize,
                    iconSize = IconSizes.xs,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RequestStatusBadge(
    status: RequestStatus,
    modifier: Modifier = Modifier,
) {
    val (label, severity) =
        when (status) {
            RequestStatus.PENDING ->
                stringResource(Res.string.request_history_status_pending) to StatusBadgeSeverity.Info
            RequestStatus.PROCESSING ->
                stringResource(Res.string.request_history_status_processing) to StatusBadgeSeverity.Warn
            RequestStatus.COMPLETED ->
                stringResource(Res.string.request_history_status_completed) to StatusBadgeSeverity.Info
            RequestStatus.FAILED ->
                stringResource(Res.string.request_history_status_failed) to StatusBadgeSeverity.Alarm
        }
    StatusBadge(label = label, severity = severity, modifier = modifier)
}

private fun formatRequestDate(isoDate: String): String {
    return if (isoDate.length >= 16) {
        "${isoDate.substring(0, 10)} ${isoDate.substring(11, 16)}"
    } else {
        isoDate.take(10)
    }
}
