package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.Recommendation
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.recommendations_dismiss
import ratatoskr.core.ui.generated.resources.recommendations_title

/**
 * Horizontally scrolling recommendations strip shown on the summary list screen.
 * Hidden automatically when there are no recommendations.
 */
@Suppress("FunctionNaming")
@Composable
fun RecommendationsSection(
    recommendations: List<Recommendation>,
    onRecommendationClick: (String) -> Unit,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recommendations.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeading(
            text = stringResource(Res.string.recommendations_title),
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            items(recommendations, key = { it.id }) { rec ->
                RecommendationCard(
                    recommendation = rec,
                    onClick = { onRecommendationClick(rec.summary.id) },
                    onDismiss = { onDismiss(rec.id) },
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RecommendationCard(
    recommendation: Recommendation,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    BrutalistCard(
        modifier =
            modifier
                .width(Dimensions.recommendationCardWidth)
                .clickable(role = Role.Button, onClick = onClick),
        contentPadding = Spacing.sm,
    ) {
        Box {
            Column {
                val imageUrl = recommendation.summary.imageUrl
                if (imageUrl != null) {
                    ProxiedImage(
                        imageUrl = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(Dimensions.recommendationCardImageHeight),
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(Dimensions.recommendationCardImageHeight),
                    )
                }
                Column(modifier = Modifier.padding(Spacing.sm)) {
                    FrostText(
                        text = recommendation.summary.title,
                        style = AppTheme.frostType.monoBody,
                        color = ink,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    recommendation.reason?.let { reason ->
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        FrostText(
                            text = reason,
                            style = AppTheme.frostType.monoSm,
                            color = ink.copy(alpha = AppTheme.alpha.secondary),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            AppIconButton(
                imageVector = AppIcons.Close,
                contentDescription = stringResource(Res.string.recommendations_dismiss),
                onClick = onDismiss,
                tint = ink.copy(alpha = AppTheme.alpha.secondary),
                iconSize = IconSizes.xs,
                buttonSize = IconSizes.lg,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}
