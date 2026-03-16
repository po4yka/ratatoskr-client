@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.Loading
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.po4yka.bitesizereader.domain.model.ReadingPreferences
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.ui.components.AddToCollectionDialog
import com.po4yka.bitesizereader.domain.usecase.GetProxiedImageUrlUseCase
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.HeaderIconButton
import com.po4yka.bitesizereader.ui.components.ProxiedImage
import com.po4yka.bitesizereader.ui.components.ProxiedImageTransformer
import com.po4yka.bitesizereader.ui.components.ReadingSettingsPanel
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.components.TagChip
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import com.po4yka.bitesizereader.util.extractDomain
import kotlin.time.Instant
import org.koin.compose.koinInject

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
            onReadingSettingsClick = { viewModel.toggleReadingSettings() },
        )

        // Reading settings panel
        ReadingSettingsPanel(
            visible = state.showReadingSettings,
            preferences = state.readingPreferences,
            onFontSizeScaleChange = { viewModel.updateFontSizeScale(it) },
            onLineSpacingScaleChange = { viewModel.updateLineSpacingScale(it) },
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
                    readingPreferences = state.readingPreferences,
                    initialScrollPosition = state.lastReadPosition,
                    initialScrollOffset = state.lastReadOffset,
                    onSaveReadPosition = { position, offset ->
                        viewModel.saveReadPosition(position, offset)
                    },
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
    onReadingSettingsClick: () -> Unit = {},
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
                    tint = if (s.isRead) Carbon.theme.supportSuccess else Carbon.theme.iconSecondary,
                    modifier = Modifier.size(IconSizes.sm),
                )

                HeaderIconButton(
                    icon = CarbonIcons.Settings,
                    contentDescription = "Reading settings",
                    onClick = onReadingSettingsClick,
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
@Composable
private fun SummaryDetailContent(
    summary: Summary,
    readingPreferences: ReadingPreferences,
    initialScrollPosition: Int,
    initialScrollOffset: Int,
    onSaveReadPosition: (position: Int, offset: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = initialScrollPosition,
            initialFirstVisibleItemScrollOffset = initialScrollOffset,
        )

    val readingProgress by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            if (layoutInfo.totalItemsCount == 0) return@derivedStateOf 0f
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf 0f
            val lastIndex = lastVisibleItem.index
            (lastIndex + 1).toFloat() / layoutInfo.totalItemsCount.toFloat()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onSaveReadPosition(
                lazyListState.firstVisibleItemIndex,
                lazyListState.firstVisibleItemScrollOffset,
            )
        }
    }

    val getProxiedImageUrlUseCase = koinInject<GetProxiedImageUrlUseCase>()
    val imageTransformer = remember { ProxiedImageTransformer(getProxiedImageUrlUseCase) }

    Column(modifier = modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { readingProgress },
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = Carbon.theme.linkPrimary,
            trackColor = Carbon.theme.borderSubtle00,
        )

        val markdownColors =
            DefaultMarkdownColors(
                text = Carbon.theme.textPrimary,
                codeBackground = Carbon.theme.layer01,
                inlineCodeBackground = Carbon.theme.layer01,
                dividerColor = Carbon.theme.borderSubtle00,
                tableBackground = Carbon.theme.layer01,
            )

        val markdownTypography =
            buildMarkdownTypography(
                fontScale = readingPreferences.fontSizeScale,
                lineScale = readingPreferences.lineSpacingScale,
            )

        val markdownContent = summary.fullContent ?: summary.content
        if (markdownContent.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No content available",
                    style = Carbon.typography.body01,
                    color = Carbon.theme.textSecondary,
                )
            }
        } else {
            Markdown(
                content = markdownContent,
                colors = markdownColors,
                typography = markdownTypography,
                imageTransformer = imageTransformer,
                modifier = Modifier.fillMaxSize().weight(1f),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Loading(modifier = Modifier.size(48.dp))
                    }
                },
                error = { content ->
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(Spacing.md),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        item(key = "header") { ArticleHeader(summary = summary) }
                        item(key = "divider_top") {
                            HorizontalDivider(color = Carbon.theme.borderSubtle00)
                            Spacer(modifier = Modifier.height(Spacing.md))
                        }
                        item(key = "fallback_text") {
                            Text(
                                text = content,
                                style = Carbon.typography.body01,
                                color = Carbon.theme.textPrimary,
                            )
                        }
                        item(key = "footer") { ArticleFooter(sourceUrl = summary.sourceUrl) }
                    }
                },
                success = { state, components, _ ->
                    val nodes = remember(state.node) { state.node.children }

                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(Spacing.md),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        item(key = "header") { ArticleHeader(summary = summary) }

                        item(key = "divider_top") {
                            HorizontalDivider(color = Carbon.theme.borderSubtle00)
                            Spacer(modifier = Modifier.height(Spacing.md))
                        }

                        items(
                            items = nodes,
                            key = { node -> "md_${node.startOffset}" },
                        ) { node ->
                            MarkdownElement(node, components, state.content)
                        }

                        item(key = "footer") { ArticleFooter(sourceUrl = summary.sourceUrl) }
                    }
                },
            )
        }
    }
}

@Suppress("FunctionNaming")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArticleHeader(summary: Summary) {
    Column {
        Text(
            text = summary.title,
            style = Carbon.typography.heading04,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(Spacing.xs))

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
            text =
                buildString {
                    append(formatDate(summary.createdAt))
                    summary.readingTimeMin?.let { append(" | $it min read") }
                },
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
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ArticleFooter(sourceUrl: String) {
    Column {
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
            text = sourceUrl,
            style = Carbon.typography.label01,
            color = Carbon.theme.linkPrimary,
            modifier =
                Modifier
                    .semantics { role = Role.Button }
                    .clickable { uriHandler.openUri(sourceUrl) },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun buildMarkdownTypography(
    fontScale: Float,
    lineScale: Float,
) = run {
    fun TextStyle.scaled(): TextStyle =
        copy(
            fontSize = fontSize * fontScale,
            lineHeight = if (lineHeight.isSp) lineHeight * lineScale else lineHeight,
        )

    val scaledBody = Carbon.typography.body01.scaled()
    markdownTypography(
        h1 = Carbon.typography.heading04.scaled(),
        h2 = Carbon.typography.heading03.scaled(),
        h3 = Carbon.typography.headingCompact01.scaled(),
        h4 = Carbon.typography.bodyCompact01.copy(fontWeight = FontWeight.Bold).scaled(),
        h5 = Carbon.typography.bodyCompact01.copy(fontWeight = FontWeight.Medium).scaled(),
        h6 = Carbon.typography.bodyCompact01.scaled(),
        paragraph = scaledBody,
        text = scaledBody,
        quote = scaledBody.copy(fontStyle = FontStyle.Italic),
        code = scaledBody.copy(fontFamily = FontFamily.Monospace),
        bullet = scaledBody,
        list = scaledBody,
        ordered = scaledBody,
    )
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
