package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.trending_topics_title
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.Spacing
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

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.trending_topics_title),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
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

@Suppress("FunctionNaming")
@Composable
private fun TrendingTopicChip(
    topic: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = topic,
        style = Carbon.typography.bodyCompact01,
        color = Carbon.theme.textOnColor,
        modifier =
            modifier
                .clip(RoundedCornerShape(Dimensions.chipCornerRadius))
                .background(Carbon.theme.linkPrimary)
                .clickable(onClick = onClick)
                .semantics { role = Role.Button }
                .padding(horizontal = Spacing.sm, vertical = Dimensions.badgeVerticalPadding + Spacing.xxs),
    )
}
