package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.insights_fallback_source
import ratatoskr.core.ui.generated.resources.insights_title
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.util.extractDomain
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun InsightsSection(
    insights: List<Summary>,
    onSummaryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (insights.isEmpty()) return

    LayerCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.xxs),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.insights_title),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
                modifier =
                    Modifier
                        .padding(horizontal = Spacing.md)
                        .padding(bottom = Spacing.sm),
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(
                    items = insights,
                    key = { it.id },
                ) { summary ->
                    InsightCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun InsightCard(
    summary: Summary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LayerCard(
        modifier = modifier.width(Dimensions.recommendationCardWidth),
        onClick = onClick,
        backgroundColor = AppTheme.colors.layer02,
    ) {
        Column {
            if (summary.imageUrl != null) {
                ProxiedImage(
                    imageUrl = summary.imageUrl!!,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                )
            }

            Column(modifier = Modifier.padding(Spacing.sm)) {
                Text(
                    text = summary.title,
                    style = AppTheme.type.headingCompact01,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(Spacing.xxs))

                Text(
                    text = extractDomain(summary.sourceUrl) ?: stringResource(Res.string.insights_fallback_source),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
