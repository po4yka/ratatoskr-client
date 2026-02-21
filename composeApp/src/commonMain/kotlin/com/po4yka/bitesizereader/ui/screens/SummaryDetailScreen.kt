@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.Loading
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.ui.components.AddToCollectionDialog
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.HeaderIconButton
import com.po4yka.bitesizereader.ui.components.ProxiedImage
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.components.TagChip
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.ReadIndicator
import com.po4yka.bitesizereader.ui.theme.Spacing
import com.po4yka.bitesizereader.util.extractDomain
import kotlin.time.Instant

/** Summary detail screen using Carbon Design System */
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
            onFavoriteClick = { viewModel.toggleFavorite() },
            onAddToCollectionClick = { viewModel.showAddToCollection() },
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

    // Add to Collection Dialog
    if (state.showAddToCollectionDialog) {
        AddToCollectionDialog(
            collections = state.collections,
            isLoading = state.isLoadingCollections,
            isAdding = state.isAddingToCollection,
            error = state.addToCollectionError,
            onCollectionSelected = { id -> viewModel.addToCollection(id) },
            onDismiss = { viewModel.dismissAddToCollection() },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun SummaryDetailHeader(
    summary: Summary?,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onAddToCollectionClick: () -> Unit = {},
) {
    ScreenHeader(
        title = summary?.let { extractDomain(it.sourceUrl) ?: "Summary" } ?: "Summary",
        isDetailScreen = true,
        onBackClick = onBackClick,
        actions = {
            summary?.let { s ->
                HeaderIconButton(
                    icon =
                        if (s.isFavorited) {
                            CarbonIcons.FavoriteFilled
                        } else {
                            CarbonIcons.Favorite
                        },
                    contentDescription = if (s.isFavorited) "Unfavorite" else "Favorite",
                    onClick = onFavoriteClick,
                    tint =
                        if (s.isFavorited) {
                            Carbon.theme.supportError
                        } else {
                            Carbon.theme.iconSecondary
                        },
                )

                HeaderIconButton(
                    icon = CarbonIcons.Folder,
                    contentDescription = "Add to collection",
                    onClick = onAddToCollectionClick,
                )

                Icon(
                    imageVector = if (s.isRead) CarbonIcons.CheckmarkFilled else CarbonIcons.CircleOutline,
                    contentDescription = if (s.isRead) "Read" else "Unread",
                    tint = if (s.isRead) ReadIndicator else Carbon.theme.iconSecondary,
                    modifier = Modifier.size(IconSizes.sm),
                )

                HeaderIconButton(
                    icon = CarbonIcons.Share,
                    contentDescription = "Share",
                    onClick = onShareClick,
                )
            }
        },
    )
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
                .padding(Spacing.md),
    ) {
        Text(
            text = summary.title,
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Hero image
        if (summary.imageUrl != null) {
            ProxiedImage(
                imageUrl = summary.imageUrl!!,
                contentDescription = summary.title,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
            )
            Spacer(modifier = Modifier.height(Spacing.md))
        }

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

        Spacer(modifier = Modifier.height(Spacing.md))

        if (summary.tags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                summary.tags.forEach { tag -> TagChip(tag = tag) }
            }
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        HorizontalDivider(color = Carbon.theme.borderSubtle00)
        Spacer(modifier = Modifier.height(Spacing.md))

        // Markdown content with Carbon-themed colors
        val markdownColors =
            DefaultMarkdownColors(
                text = Carbon.theme.textPrimary,
                codeBackground = Carbon.theme.layer01,
                inlineCodeBackground = Carbon.theme.layer01,
                dividerColor = Carbon.theme.borderSubtle00,
                tableBackground = Carbon.theme.layer01,
            )
        val markdownTypography =
            markdownTypography(
                h1 = Carbon.typography.heading04,
                h2 = Carbon.typography.heading03,
                h3 = Carbon.typography.headingCompact01,
                h4 = Carbon.typography.headingCompact01,
                h5 = Carbon.typography.headingCompact01,
                h6 = Carbon.typography.headingCompact01,
                paragraph = Carbon.typography.body01,
                text = Carbon.typography.body01,
                quote = Carbon.typography.body01.copy(fontStyle = FontStyle.Italic),
                code = Carbon.typography.body01.copy(fontFamily = FontFamily.Monospace),
                bullet = Carbon.typography.body01,
                list = Carbon.typography.body01,
                ordered = Carbon.typography.body01,
            )

        Markdown(
            content = summary.content,
            colors = markdownColors,
            typography = markdownTypography,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        HorizontalDivider(color = Carbon.theme.borderSubtle00)
        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Original Article",
            style = Carbon.typography.headingCompact01,
            color = Carbon.theme.textPrimary,
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        val uriHandler = LocalUriHandler.current
        Text(
            text = summary.sourceUrl,
            style = Carbon.typography.label01,
            color = Carbon.theme.linkPrimary,
            modifier = Modifier.clickable { uriHandler.openUri(summary.sourceUrl) },
        )
    }
}

private fun formatDate(instant: Instant): String {
    val civilDate = civilFromDays((instant.epochSeconds / 86400).toInt())
    val monthNames =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${monthNames[civilDate.month - 1]} ${civilDate.day.toString().padStart(2, '0')}, ${civilDate.year}"
}

private data class CivilDate(val year: Int, val month: Int, val day: Int)

/** Converts days since Unix epoch to a civil date using Howard Hinnant's algorithm. */
private fun civilFromDays(daysSinceEpoch: Int): CivilDate {
    val z = daysSinceEpoch + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1461 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = mp + (if (mp < 10) 3 else -9)
    return CivilDate(y + (if (m <= 2) 1 else 0), m, d)
}
