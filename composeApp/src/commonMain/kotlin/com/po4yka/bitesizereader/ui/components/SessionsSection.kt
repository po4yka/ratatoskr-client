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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.ui.icons.CarbonIcons

/**
 * Expandable section showing the user's active sessions.
 */
@Suppress("FunctionNaming")
@Composable
fun SessionsSection(
    sessions: List<Session>,
    isLoading: Boolean,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
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
                    text = "Active Sessions",
                    style = Carbon.typography.headingCompact01,
                    color = Carbon.theme.textPrimary,
                )
                Text(
                    text =
                        if (sessions.isNotEmpty()) {
                            "${sessions.size} device${if (sessions.size != 1) "s" else ""}"
                        } else {
                            "View your active sessions"
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
                            // ArrowLeft points left; rotate to right (180) or down (90)
                            rotationZ = if (isExpanded) 90f else 180f
                        },
            )
        }

        // Sessions list (collapsible)
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
                                text = "Loading sessions...",
                                style = Carbon.typography.label01,
                                color = Carbon.theme.textSecondary,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                    sessions.isEmpty() -> {
                        Text(
                            text = "No sessions found",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                    else -> {
                        sessions.forEach { session ->
                            SessionItem(session = session)
                        }
                    }
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SessionItem(
    session: Session,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer02)
                .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = session.deviceInfo ?: session.clientId ?: "Unknown Device",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            if (session.isCurrent) {
                Text(
                    text = "Current",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textOnColor,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Carbon.theme.supportSuccess)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }

        session.ipAddress?.let { ip ->
            Text(
                text = "IP: $ip",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }

        session.lastUsedAt?.let { lastUsed ->
            Text(
                text = "Last active: ${formatSessionDate(lastUsed)}",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}

private fun formatSessionDate(isoDate: String): String {
    // Simple formatting - just extract date and time parts
    return if (isoDate.length >= 16) {
        "${isoDate.substring(0, 10)} ${isoDate.substring(11, 16)}"
    } else {
        isoDate.take(10)
    }
}
