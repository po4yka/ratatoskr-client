package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import ratatoskr.core.ui.generated.resources.summary_card_accessibility_favorited
import ratatoskr.core.ui.generated.resources.summary_card_accessibility_read_article
import ratatoskr.core.ui.generated.resources.summary_card_accessibility_reading_time
import ratatoskr.core.ui.generated.resources.summary_card_accessibility_source
import ratatoskr.core.ui.generated.resources.summary_card_accessibility_unread_article
import ratatoskr.core.ui.generated.resources.summary_card_available_offline
import ratatoskr.core.ui.generated.resources.summary_card_favorited
import ratatoskr.core.ui.generated.resources.summary_card_saved_article
import ratatoskr.core.ui.generated.resources.summary_detail_mark_read

@Suppress("FunctionNaming", "LongMethod", "LongParameterList")
@Composable
fun SummaryGridCard(
    summary: Summary,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onMarkReadClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onAddToCollectionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val source = extractDomain(summary.sourceUrl) ?: stringResource(Res.string.summary_card_saved_article)
    val cardDescription =
        buildString {
            append(summary.title)
            append(". ")
            append(
                if (summary.isRead) {
                    stringResource(Res.string.summary_card_accessibility_read_article)
                } else {
                    stringResource(Res.string.summary_card_accessibility_unread_article)
                },
            )
            append(" ")
            append(stringResource(Res.string.summary_card_accessibility_source, source))
            if (summary.isFavorited) {
                append(" ")
                append(stringResource(Res.string.summary_card_accessibility_favorited))
            }
            summary.readingTimeMin?.let {
                append(" ")
                append(stringResource(Res.string.summary_card_accessibility_reading_time, it))
            }
        }

    BrutalistCard(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics { contentDescription = cardDescription }
                .clickable(role = Role.Button, onClick = onClick),
        contentPadding = Spacing.xs,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // Hero image
            if (summary.imageUrl != null) {
                ProxiedImage(
                    imageUrl = summary.imageUrl!!,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RectangleShape),
                )
            }

            // Title + overflow menu row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                FrostText(
                    text = summary.title,
                    style = AppTheme.frostType.monoEmph,
                    color = AppTheme.frostColors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                SummaryOverflowMenu(
                    summary = summary,
                    onFavoriteClick = onFavoriteClick,
                    onMarkReadClick = onMarkReadClick,
                    onAddToCollectionClick = onAddToCollectionClick,
                    onDeleteClick = onDeleteClick,
                )
            }

            // Status indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (summary.isFullContentCached) {
                    Icon(
                        imageVector = AppIcons.Download,
                        contentDescription = stringResource(Res.string.summary_card_available_offline),
                        tint = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                        modifier = Modifier.size(IconSizes.xs),
                    )
                }
                if (summary.isFavorited) {
                    Icon(
                        imageVector = AppIcons.FavoriteFilled,
                        contentDescription = stringResource(Res.string.summary_card_favorited),
                        tint = AppTheme.colors.supportError,
                        modifier = Modifier.size(IconSizes.xs),
                    )
                }
                if (summary.isRead) {
                    Box(
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clip(RectangleShape)
                                .background(AppTheme.colors.supportSuccess),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = AppIcons.Checkmark,
                            contentDescription = stringResource(Res.string.summary_detail_mark_read),
                            tint = AppTheme.colors.textOnColor,
                            modifier = Modifier.size(IconSizes.xs),
                        )
                    }
                }
            }

            // Source and reading time
            FrostText(
                text =
                    buildString {
                        append(source)
                        summary.readingTimeMin?.let {
                            append(" | ")
                            append(stringResource(Res.string.custom_digest_create_read_time, it))
                        }
                    },
                style = AppTheme.frostType.monoSm,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
