package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.util.extractDomain
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.custom_digest_create_read_time
import ratatoskr.core.ui.generated.resources.summary_card_available_offline
import ratatoskr.core.ui.generated.resources.summary_card_favorited
import ratatoskr.core.ui.generated.resources.summary_card_saved_article
import ratatoskr.core.ui.generated.resources.summary_detail_mark_read

/** Summary card for list rows. */
@Suppress("FunctionNaming", "LongMethod", "LongParameterList")
@Composable
fun SummaryCard(
    summary: Summary,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onMarkReadClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onAddToCollectionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BrutalistCard(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick),
        contentPadding = Spacing.md,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (summary.imageUrl != null) {
                ProxiedImage(
                    imageUrl = summary.imageUrl!!,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(64.dp)
                            .clip(RectangleShape),
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            Column(modifier = Modifier.weight(1f)) {
                FrostText(
                    text = summary.title,
                    style = AppTheme.frostType.monoEmph,
                    color = AppTheme.frostColors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (summary.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    FrostText(
                        text = summary.content.take(150).replace("\n", " "),
                        style = AppTheme.frostType.monoBody,
                        color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xxs))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FrostText(
                        text =
                            buildString {
                                append(
                                    extractDomain(summary.sourceUrl)
                                        ?: stringResource(Res.string.summary_card_saved_article),
                                )
                                summary.readingTimeMin?.let {
                                    append(" | ")
                                    append(stringResource(Res.string.custom_digest_create_read_time, it))
                                }
                            },
                        style = AppTheme.frostType.monoSm,
                        color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    if (summary.isFavorited) {
                        FrostIcon(
                            imageVector = AppIcons.FavoriteFilled,
                            contentDescription = stringResource(Res.string.summary_card_favorited),
                            tint = AppTheme.colors.supportError,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }

                    if (summary.isFullContentCached) {
                        FrostIcon(
                            imageVector = AppIcons.Download,
                            contentDescription = stringResource(Res.string.summary_card_available_offline),
                            tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }

                    if (summary.isRead) {
                        FrostIcon(
                            imageVector = AppIcons.CheckmarkFilled,
                            contentDescription = stringResource(Res.string.summary_detail_mark_read),
                            tint = AppTheme.colors.supportSuccess,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Spacing.xs))

            SummaryOverflowMenu(
                summary = summary,
                onFavoriteClick = onFavoriteClick,
                onMarkReadClick = onMarkReadClick,
                onAddToCollectionClick = onAddToCollectionClick,
                onDeleteClick = onDeleteClick,
            )
        }
    }
}
