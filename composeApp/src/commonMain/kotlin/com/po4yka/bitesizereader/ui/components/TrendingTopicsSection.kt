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
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon

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
                .padding(16.dp),
    ) {
        Text(
            text = "Trending Topics",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                .clip(RoundedCornerShape(16.dp))
                .background(Carbon.theme.linkPrimary)
                .clickable(onClick = onClick)
                .semantics { role = Role.Button }
                .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
