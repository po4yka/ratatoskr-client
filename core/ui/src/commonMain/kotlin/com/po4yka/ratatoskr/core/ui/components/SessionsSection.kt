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
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.RowDigest
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadge
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadgeSeverity
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.Session
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.sessions_collapse
import ratatoskr.core.ui.generated.resources.sessions_count_plural
import ratatoskr.core.ui.generated.resources.sessions_count_singular
import ratatoskr.core.ui.generated.resources.sessions_current
import ratatoskr.core.ui.generated.resources.sessions_empty
import ratatoskr.core.ui.generated.resources.sessions_expand
import ratatoskr.core.ui.generated.resources.sessions_ip
import ratatoskr.core.ui.generated.resources.sessions_last_active
import ratatoskr.core.ui.generated.resources.sessions_loading
import ratatoskr.core.ui.generated.resources.sessions_prompt
import ratatoskr.core.ui.generated.resources.sessions_title
import ratatoskr.core.ui.generated.resources.sessions_unknown_device

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
                    SectionHeading(text = stringResource(Res.string.sessions_title))
                    FrostText(
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
                        style = AppTheme.frostType.monoSm,
                        color = ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }

                FrostIcon(
                    imageVector = AppIcons.ArrowLeft,
                    contentDescription =
                        if (isExpanded) {
                            stringResource(Res.string.sessions_collapse)
                        } else {
                            stringResource(Res.string.sessions_expand)
                        },
                    tint = ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier =
                        Modifier
                            .size(IconSizes.xs)
                            .graphicsLayer { rotationZ = if (isExpanded) 90f else 180f },
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
                                AppSmallSpinner()
                                FrostText(
                                    text = stringResource(Res.string.sessions_loading),
                                    style = AppTheme.frostType.monoSm,
                                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                                    modifier = Modifier.padding(start = Spacing.xs),
                                )
                            }
                        }
                        sessions.isEmpty() -> {
                            FrostText(
                                text = stringResource(Res.string.sessions_empty),
                                style = AppTheme.frostType.monoBody,
                                color = ink.copy(alpha = AppTheme.alpha.secondary),
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
}

@Suppress("FunctionNaming")
@Composable
private fun SessionItem(
    session: Session,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    BrutalistCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = Spacing.sm,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FrostText(
                    text =
                        session.deviceInfo ?: session.clientId
                            ?: stringResource(Res.string.sessions_unknown_device),
                    style = AppTheme.frostType.monoBody,
                    color = ink,
                    modifier = Modifier.weight(1f),
                )
                if (session.isCurrent) {
                    StatusBadge(
                        label = stringResource(Res.string.sessions_current),
                        severity = StatusBadgeSeverity.Info,
                    )
                }
            }

            session.ipAddress?.let { ip ->
                RowDigest(
                    label = stringResource(Res.string.sessions_ip, ""),
                    value = ip,
                )
            }

            session.lastUsedAt?.let { lastUsed ->
                RowDigest(
                    label = stringResource(Res.string.sessions_last_active, ""),
                    value = formatSessionDate(lastUsed),
                )
            }
        }
    }
}

private fun formatSessionDate(isoDate: String): String {
    return if (isoDate.length >= 16) {
        "${isoDate.substring(0, 10)} ${isoDate.substring(11, 16)}"
    } else {
        isoDate.take(10)
    }
}
