package com.po4yka.bitesizereader.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.custom_digest_create_read_time
import bitesizereader.core.ui.generated.resources.summary_card_available_offline
import bitesizereader.core.ui.generated.resources.summary_card_favorited
import bitesizereader.core.ui.generated.resources.summary_card_saved_article
import bitesizereader.core.ui.generated.resources.summary_detail_mark_read
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
import com.po4yka.bitesizereader.util.extractDomain
import org.jetbrains.compose.resources.stringResource

/**
 * Card component for displaying a summary in a list using Carbon Design System
 */
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
    CarbonLayerCard(
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
                            .clip(RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.title,
                    style = Carbon.typography.headingCompact01,
                    color = Carbon.theme.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (summary.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = summary.content.take(150).replace("\n", " "),
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
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
                                append(extractDomain(summary.sourceUrl) ?: stringResource(Res.string.summary_card_saved_article))
                                summary.readingTimeMin?.let {
                                    append(" | ")
                                    append(stringResource(Res.string.custom_digest_create_read_time, it))
                                }
                            },
                        style = Carbon.typography.label01,
                        color = Carbon.theme.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    if (summary.isFavorited) {
                        Icon(
                            imageVector = CarbonIcons.FavoriteFilled,
                            contentDescription = stringResource(Res.string.summary_card_favorited),
                            tint = Carbon.theme.supportError,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }

                    if (summary.isFullContentCached) {
                        Icon(
                            imageVector = CarbonIcons.Download,
                            contentDescription = stringResource(Res.string.summary_card_available_offline),
                            tint = Carbon.theme.iconSecondary,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }

                    if (summary.isRead) {
                        Icon(
                            imageVector = CarbonIcons.CheckmarkFilled,
                            contentDescription = stringResource(Res.string.summary_detail_mark_read),
                            tint = Carbon.theme.supportSuccess,
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
