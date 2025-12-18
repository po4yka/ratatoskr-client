@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.components

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.ui.icons.CarbonIcons

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
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01),
    ) {
        // Header (clickable to expand/collapse)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpanded)
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Request History",
                    style = Carbon.typography.headingCompact01,
                    color = Carbon.theme.textPrimary,
                )
                Text(
                    text =
                        if (requests.isNotEmpty()) {
                            "${requests.size} request${if (requests.size != 1) "s" else ""}"
                        } else {
                            "View your URL submission history"
                        },
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }

            Icon(
                imageVector = CarbonIcons.ArrowLeft,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Carbon.theme.iconSecondary,
                modifier =
                    Modifier
                        .size(16.dp)
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
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when {
                    isLoading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SmallLoading()
                            Text(
                                text = "Loading requests...",
                                style = Carbon.typography.label01,
                                color = Carbon.theme.textSecondary,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                    requests.isEmpty() -> {
                        Text(
                            text = "No requests yet",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
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
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer02)
                .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = request.url,
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(status = request.status)
                Text(
                    text = formatRequestDate(request.updatedAt.toString()),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        }

        if (request.status == RequestStatus.FAILED) {
            IconButton(
                onClick = onRetry,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = CarbonIcons.Renew,
                    contentDescription = "Retry",
                    tint = Carbon.theme.iconPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
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
            RequestStatus.PENDING -> Triple("Pending", Carbon.theme.layer02, Carbon.theme.textSecondary)
            RequestStatus.PROCESSING -> Triple("Processing", Carbon.theme.linkPrimary, Carbon.theme.textOnColor)
            RequestStatus.COMPLETED -> Triple("Completed", Carbon.theme.supportSuccess, Carbon.theme.textOnColor)
            RequestStatus.FAILED -> Triple("Failed", Carbon.theme.supportError, Carbon.theme.textOnColor)
        }

    Text(
        text = text,
        style = Carbon.typography.label01,
        color = textColor,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

private fun formatRequestDate(isoDate: String): String {
    return if (isoDate.length >= 16) {
        "${isoDate.substring(0, 10)} ${isoDate.substring(11, 16)}"
    } else {
        isoDate.take(10)
    }
}
