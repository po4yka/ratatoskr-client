package com.po4yka.bitesizereader.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.custom_digest_create_read_time
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_favorited
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_read_article
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_reading_time
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_source
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_unread_article
import bitesizereader.core.ui.generated.resources.summary_card_available_offline
import bitesizereader.core.ui.generated.resources.summary_card_favorited
import bitesizereader.core.ui.generated.resources.summary_card_saved_article
import bitesizereader.core.ui.generated.resources.summary_detail_mark_read
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
import com.po4yka.bitesizereader.util.extractDomain
import org.jetbrains.compose.resources.stringResource

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

    CarbonLayerCard(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics { contentDescription = cardDescription },
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.xs),
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
                        .clip(RoundedCornerShape(Dimensions.cardCornerRadius)),
            )
        }

        // Title + overflow menu row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = summary.title,
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
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
                    imageVector = CarbonIcons.Download,
                    contentDescription = stringResource(Res.string.summary_card_available_offline),
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(IconSizes.xs),
                )
            }
            if (summary.isFavorited) {
                Icon(
                    imageVector = CarbonIcons.FavoriteFilled,
                    contentDescription = stringResource(Res.string.summary_card_favorited),
                    tint = Carbon.theme.supportError,
                    modifier = Modifier.size(IconSizes.xs),
                )
            }
            if (summary.isRead) {
                Box(
                    modifier =
                        Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Carbon.theme.supportSuccess),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = CarbonIcons.Checkmark,
                        contentDescription = stringResource(Res.string.summary_detail_mark_read),
                        tint = Carbon.theme.textOnColor,
                        modifier = Modifier.size(IconSizes.xs),
                    )
                }
            }
        }

        // Source and reading time
        Text(
            text =
                buildString {
                    append(source)
                    summary.readingTimeMin?.let {
                        append(" | ")
                        append(stringResource(Res.string.custom_digest_create_read_time, it))
                    }
                },
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        }
    }
}
