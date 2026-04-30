package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.custom_digest_create_read_time
import ratatoskr.core.ui.generated.resources.summary_card_available_offline
import ratatoskr.core.ui.generated.resources.summary_card_favorited
import ratatoskr.core.ui.generated.resources.summary_card_saved_article
import ratatoskr.core.ui.generated.resources.summary_detail_mark_read
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.util.extractDomain
import org.jetbrains.compose.resources.stringResource

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
    LayerCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
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
                Text(
                    text = summary.title,
                    style = AppTheme.type.headingCompact01,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (summary.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = summary.content.take(150).replace("\n", " "),
                        style = AppTheme.type.bodyCompact01,
                        color = AppTheme.colors.textSecondary,
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
                    Text(
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
                        style = AppTheme.type.label01,
                        color = AppTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    if (summary.isFavorited) {
                        Icon(
                            imageVector = AppIcons.FavoriteFilled,
                            contentDescription = stringResource(Res.string.summary_card_favorited),
                            tint = AppTheme.colors.supportError,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }

                    if (summary.isFullContentCached) {
                        Icon(
                            imageVector = AppIcons.Download,
                            contentDescription = stringResource(Res.string.summary_card_available_offline),
                            tint = AppTheme.colors.iconSecondary,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }

                    if (summary.isRead) {
                        Icon(
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
