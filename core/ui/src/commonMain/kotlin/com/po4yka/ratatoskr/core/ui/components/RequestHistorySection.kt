@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
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
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.RequestStatus
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

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
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(AppTheme.colors.layer01),
    ) {
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
            Column {
                Text(
                    text = stringResource(Res.string.submit_url_request_history),
                    style = AppTheme.type.headingCompact01,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
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
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                )
            }

            Icon(
                imageVector = AppIcons.ArrowLeft,
                contentDescription =
                    if (isExpanded) {
                        stringResource(Res.string.request_history_collapse)
                    } else {
                        stringResource(Res.string.request_history_expand)
                    },
                tint = AppTheme.colors.iconSecondary,
                modifier =
                    Modifier
                        .size(IconSizes.xs)
                        .graphicsLayer {
                            rotationZ = if (isExpanded) 90f else 180f
                        },
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
                            Text(
                                text = stringResource(Res.string.request_history_loading),
                                style = AppTheme.type.label01,
                                color = AppTheme.colors.textSecondary,
                                modifier = Modifier.padding(start = Spacing.xs),
                            )
                        }
                    }
                    requests.isEmpty() -> {
                        Text(
                            text = stringResource(Res.string.request_history_empty),
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textSecondary,
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

@Suppress("FunctionNaming")
@Composable
private fun RequestItem(
    request: Request,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(AppTheme.colors.layer02)
                .padding(Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = request.url,
                style = AppTheme.type.bodyCompact01,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(status = request.status)
                Text(
                    text = formatRequestDate(request.updatedAt.toString()),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                )
            }
        }

        if (request.status == RequestStatus.FAILED) {
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

@Suppress("FunctionNaming")
@Composable
private fun StatusBadge(
    status: RequestStatus,
    modifier: Modifier = Modifier,
) {
    val (text, backgroundColor, textColor) =
        when (status) {
            RequestStatus.PENDING ->
                Triple(
                    stringResource(Res.string.request_history_status_pending),
                    AppTheme.colors.layer02,
                    AppTheme.colors.textSecondary,
                )
            RequestStatus.PROCESSING ->
                Triple(
                    stringResource(Res.string.request_history_status_processing),
                    AppTheme.colors.linkPrimary,
                    AppTheme.colors.textOnColor,
                )
            RequestStatus.COMPLETED ->
                Triple(
                    stringResource(Res.string.request_history_status_completed),
                    AppTheme.colors.supportSuccess,
                    AppTheme.colors.textOnColor,
                )
            RequestStatus.FAILED ->
                Triple(
                    stringResource(Res.string.request_history_status_failed),
                    AppTheme.colors.supportError,
                    AppTheme.colors.textOnColor,
                )
        }

    Text(
        text = text,
        style = AppTheme.type.label01,
        color = textColor,
        modifier =
            modifier
                .clip(RoundedCornerShape(Dimensions.badgeCornerRadius))
                .background(backgroundColor)
                .padding(
                    horizontal = Dimensions.badgeHorizontalPadding,
                    vertical = Dimensions.badgeVerticalPadding,
                ),
    )
}

private fun formatRequestDate(isoDate: String): String {
    return if (isoDate.length >= 16) {
        "${isoDate.substring(0, 10)} ${isoDate.substring(11, 16)}"
    } else {
        isoDate.take(10)
    }
}
