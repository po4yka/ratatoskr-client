@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.CheckCircle
import compose.icons.feathericons.Circle
import compose.icons.feathericons.Share2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.Loading
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.TagChip
import com.po4yka.bitesizereader.ui.theme.ReadIndicator
import kotlin.time.Instant

/**
 * Summary detail screen using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun SummaryDetailScreen(
    viewModel: SummaryDetailViewModel,
    summaryId: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(summaryId) {
        viewModel.loadSummary(summaryId)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Header
        SummaryDetailHeader(
            summary = state.summary,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )

        // Content
        when {
            state.error != null -> {
                ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.loadSummary(summaryId) },
                    modifier = Modifier.weight(1f),
                )
            }
            state.isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Loading(modifier = Modifier.size(88.dp))
                }
            }
            state.summary != null -> {
                SummaryDetailContent(
                    summary = state.summary!!,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SummaryDetailHeader(
    summary: Summary?,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Carbon.theme.layer01)
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = FeatherIcons.ArrowLeft,
                contentDescription = "Back",
                tint = Carbon.theme.iconPrimary,
            )
        }

        Text(
            text = "Summary",
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        summary?.let { s ->
            Icon(
                imageVector = if (s.isRead) FeatherIcons.CheckCircle else FeatherIcons.Circle,
                contentDescription = if (s.isRead) "Read" else "Unread",
                tint = if (s.isRead) ReadIndicator else Carbon.theme.iconSecondary,
                modifier = Modifier.size(20.dp),
            )

            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = FeatherIcons.Share2,
                    contentDescription = "Share",
                    tint = Carbon.theme.iconPrimary,
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SummaryDetailContent(
    summary: Summary,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        Text(
            text = summary.title,
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = extractDomain(summary.sourceUrl) ?: "Unknown source",
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )

        Text(
            text = formatDate(summary.createdAt),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (summary.tags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                summary.tags.forEach { tag -> TagChip(tag = tag) }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        HorizontalDivider(color = Carbon.theme.borderSubtle00)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = summary.content,
            style = Carbon.typography.body01,
            color = Carbon.theme.textPrimary,
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = Carbon.theme.borderSubtle00)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Original Article",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = summary.sourceUrl,
            style = Carbon.typography.label01,
            color = Carbon.theme.linkPrimary,
        )
    }
}

private fun formatDate(instant: Instant): String {
    val epochSeconds = instant.epochSeconds
    // Convert epoch seconds to date components
    // Using a simplified calculation for date formatting
    val days = (epochSeconds / 86400).toInt() + 719468 // Days since year 0
    var year = (10000L * days.toLong() + 14780) / 3652425
    var doy = days - (365 * year + year / 4 - year / 100 + year / 400).toInt()
    if (doy < 0) {
        year--
        doy = days - (365 * year + year / 4 - year / 100 + year / 400).toInt()
    }
    val mi = (100 * doy + 52) / 3060
    val month = (mi + 2) % 12 + 1
    year += (mi + 2) / 12
    val day = doy - (mi * 306 + 5) / 10 + 1

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${monthNames[month - 1]} ${day.toString().padStart(2, '0')}, $year"
}

private fun extractDomain(url: String): String? {
    val noProtocol = url.substringAfter("://", url)
    return noProtocol.substringBefore("/").ifBlank { null }
}
