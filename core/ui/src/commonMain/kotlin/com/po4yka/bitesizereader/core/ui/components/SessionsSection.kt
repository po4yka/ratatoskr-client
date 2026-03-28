package com.po4yka.bitesizereader.core.ui.components

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
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.sessions_collapse
import bitesizereader.core.ui.generated.resources.sessions_count_plural
import bitesizereader.core.ui.generated.resources.sessions_count_singular
import bitesizereader.core.ui.generated.resources.sessions_current
import bitesizereader.core.ui.generated.resources.sessions_empty
import bitesizereader.core.ui.generated.resources.sessions_expand
import bitesizereader.core.ui.generated.resources.sessions_ip
import bitesizereader.core.ui.generated.resources.sessions_last_active
import bitesizereader.core.ui.generated.resources.sessions_loading
import bitesizereader.core.ui.generated.resources.sessions_prompt
import bitesizereader.core.ui.generated.resources.sessions_title
import bitesizereader.core.ui.generated.resources.sessions_unknown_device
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

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
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(Carbon.theme.layer01),
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
                    text = stringResource(Res.string.sessions_title),
                    style = Carbon.typography.headingCompact01,
                    color = Carbon.theme.textPrimary,
                )
                Text(
                    text =
                        if (sessions.isNotEmpty()) {
                            if (sessions.size == 1) {
                                stringResource(Res.string.sessions_count_singular, sessions.size)
                            } else {
                                stringResource(Res.string.sessions_count_plural, sessions.size)
                            }
                        } else {
                            stringResource(Res.string.sessions_prompt)
                        },
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }

            Icon(
                imageVector = CarbonIcons.ArrowLeft,
                contentDescription =
                    if (isExpanded) {
                        stringResource(Res.string.sessions_collapse)
                    } else {
                        stringResource(Res.string.sessions_expand)
                    },
                tint = Carbon.theme.iconSecondary,
                modifier =
                    Modifier
                        .size(IconSizes.xs)
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
                            SmallLoading()
                            Text(
                                text = stringResource(Res.string.sessions_loading),
                                style = Carbon.typography.label01,
                                color = Carbon.theme.textSecondary,
                                modifier = Modifier.padding(start = Spacing.xs),
                            )
                        }
                    }
                    sessions.isEmpty() -> {
                        Text(
                            text = stringResource(Res.string.sessions_empty),
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
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .background(Carbon.theme.layer02)
                .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = session.deviceInfo ?: session.clientId ?: stringResource(Res.string.sessions_unknown_device),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )
            if (session.isCurrent) {
                Text(
                    text = stringResource(Res.string.sessions_current),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textOnColor,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(Dimensions.badgeCornerRadius))
                            .background(Carbon.theme.supportSuccess)
                            .padding(
                                horizontal = Dimensions.badgeHorizontalPadding,
                                vertical = Dimensions.badgeVerticalPadding,
                            ),
                )
            }
        }

        session.ipAddress?.let { ip ->
            Text(
                text = stringResource(Res.string.sessions_ip, ip),
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )
        }

        session.lastUsedAt?.let { lastUsed ->
            Text(
                text = stringResource(Res.string.sessions_last_active, formatSessionDate(lastUsed)),
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
