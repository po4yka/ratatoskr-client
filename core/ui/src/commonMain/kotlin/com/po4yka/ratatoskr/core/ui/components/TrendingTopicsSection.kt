package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.trending_topics_title
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

/**
 * Section that displays trending topics as clickable chips.
 * Shown when search is active but the query is empty.
 */
@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionNaming")
@Composable
fun TrendingTopicsSection(
    topics: List<String>,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (topics.isEmpty()) return

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
                    .padding(Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.trending_topics_title),
                style = AppTheme.type.label01,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                topics.forEach { topic ->
                    TrendingTopicChip(
                        topic = topic,
                        onClick = { onTopicClick(topic) },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun TrendingTopicChip(
    topic: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectableChip(
        label = topic,
        selected = true,
        onClick = onClick,
        modifier = modifier,
        role = Role.Button,
        contentPadding =
            PaddingValues(
                horizontal = Spacing.sm,
                vertical = Dimensions.badgeVerticalPadding + Spacing.xxs,
            ),
    )
}
