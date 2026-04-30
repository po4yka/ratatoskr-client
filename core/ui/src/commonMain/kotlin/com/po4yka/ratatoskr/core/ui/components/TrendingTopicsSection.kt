package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.MultiSelectChip
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.trending_topics_title

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

    BrutalistCard(
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
            SectionHeading(
                text = stringResource(Res.string.trending_topics_title),
                modifier = Modifier.padding(bottom = Spacing.sm),
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                topics.forEach { topic ->
                    MultiSelectChip(
                        label = topic,
                        selected = true,
                        onToggle = { onTopicClick(topic) },
                    )
                }
            }
        }
    }
}
