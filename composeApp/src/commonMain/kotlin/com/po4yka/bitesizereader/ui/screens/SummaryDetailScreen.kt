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
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.Loading
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.po4yka.bitesizereader.domain.model.Highlight
import com.po4yka.bitesizereader.domain.model.HighlightColor
import com.po4yka.bitesizereader.domain.model.ReadingPreferences
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.ui.components.AddToCollectionDialog
import com.po4yka.bitesizereader.ui.components.AnnotationDialog
import com.po4yka.bitesizereader.domain.usecase.GetProxiedImageUrlUseCase
import com.po4yka.bitesizereader.ui.components.ErrorView
import com.po4yka.bitesizereader.ui.components.HeaderIconButton
import com.po4yka.bitesizereader.ui.components.ProxiedImage
import com.po4yka.bitesizereader.ui.components.ProxiedImageTransformer
import com.po4yka.bitesizereader.ui.components.ReadingSettingsPanel
import com.po4yka.bitesizereader.ui.components.ScreenHeader
import com.po4yka.bitesizereader.ui.components.TagChip
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.HighlightBlue
import com.po4yka.bitesizereader.ui.theme.HighlightGreen
import com.po4yka.bitesizereader.ui.theme.HighlightPink
import com.po4yka.bitesizereader.ui.theme.HighlightYellow
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
            onHighlightModeClick = { viewModel.toggleHighlightMode() },
            isHighlightModeActive = state.isHighlightModeActive,
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
                    highlights = state.highlights,
                    highlightedNodeOffsets = state.highlightedNodeOffsets,
                    isHighlightModeActive = state.isHighlightModeActive,
                    onToggleHighlight = { nodeOffset, text ->
                        viewModel.toggleHighlight(nodeOffset, text)
                    },
                    onOpenAnnotationEditor = { highlightId ->
                        viewModel.openAnnotationEditor(highlightId)
                    },
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

    // Annotation Dialog
    if (state.editingAnnotationHighlightId != null) {
        AnnotationDialog(
            draft = state.annotationDraft,
            onDraftChange = { viewModel.updateAnnotationDraft(it) },
            onSave = { viewModel.saveAnnotation() },
            onCancel = { viewModel.closeAnnotationEditor() },
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
    onHighlightModeClick: () -> Unit = {},
    isHighlightModeActive: Boolean = false,
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

                HeaderIconButton(
                    icon = if (isHighlightModeActive) CarbonIcons.BookmarkAdd else CarbonIcons.Bookmark,
                    contentDescription = if (isHighlightModeActive) "Exit highlight mode" else "Highlight mode",
                    onClick = onHighlightModeClick,
                    tint = if (isHighlightModeActive) Carbon.theme.linkPrimary else Carbon.theme.iconSecondary,
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

@Suppress("FunctionNaming", "LongParameterList")
@Composable
private fun SummaryDetailContent(
    summary: Summary,
    readingPreferences: ReadingPreferences,
    initialScrollPosition: Int,
    initialScrollOffset: Int,
    highlights: List<Highlight>,
    highlightedNodeOffsets: Set<Int>,
    isHighlightModeActive: Boolean,
    onToggleHighlight: (nodeOffset: Int, text: String) -> Unit,
    onOpenAnnotationEditor: (highlightId: String) -> Unit,
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

        val textPrimary = Carbon.theme.textPrimary
        val layer01 = Carbon.theme.layer01
        val borderSubtle00 = Carbon.theme.borderSubtle00
        val markdownColors =
            remember(textPrimary, layer01, borderSubtle00) {
                DefaultMarkdownColors(
                    text = textPrimary,
                    codeBackground = layer01,
                    inlineCodeBackground = layer01,
                    dividerColor = borderSubtle00,
                    tableBackground = layer01,
                )
            }

        val heading04 = Carbon.typography.heading04
        val heading03 = Carbon.typography.heading03
        val headingCompact01 = Carbon.typography.headingCompact01
        val bodyCompact01 = Carbon.typography.bodyCompact01
        val body01 = Carbon.typography.body01
        val markdownTypography =
            remember(
                readingPreferences.fontSizeScale,
                readingPreferences.lineSpacingScale,
                heading04,
                heading03,
                headingCompact01,
                bodyCompact01,
                body01,
            ) {
                buildMarkdownTypography(
                    fontScale = readingPreferences.fontSizeScale,
                    lineScale = readingPreferences.lineSpacingScale,
                    heading04 = heading04,
                    heading03 = heading03,
                    headingCompact01 = headingCompact01,
                    bodyCompact01 = bodyCompact01,
                    body01 = body01,
                )
            }

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
                error = { _ ->
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
                                text = markdownContent,
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
                            contentType = { "markdown_node" },
                        ) { node ->
                            val isHighlighted = node.startOffset in highlightedNodeOffsets
                            val highlight = highlights.find { it.nodeOffset == node.startOffset }
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (isHighlighted) {
                                                Modifier.background(
                                                    color =
                                                        when (highlight?.color) {
                                                            HighlightColor.GREEN -> HighlightGreen.copy(alpha = 0.15f)
                                                            HighlightColor.BLUE -> HighlightBlue.copy(alpha = 0.15f)
                                                            HighlightColor.PINK -> HighlightPink.copy(alpha = 0.15f)
                                                            else -> HighlightYellow.copy(alpha = 0.2f)
                                                        },
                                                )
                                            } else {
                                                Modifier
                                            },
                                        )
                                        .then(
                                            if (isHighlightModeActive) {
                                                Modifier.clickable {
                                                    val text =
                                                        state.content.let { content ->
                                                            if (node.startOffset < content.length &&
                                                                node.endOffset <= content.length
                                                            ) {
                                                                content.substring(node.startOffset, node.endOffset)
                                                            } else {
                                                                ""
                                                            }
                                                        }
                                                    onToggleHighlight(node.startOffset, text)
                                                }
                                            } else if (isHighlighted && highlight != null) {
                                                Modifier.clickable { onOpenAnnotationEditor(highlight.id) }
                                            } else {
                                                Modifier
                                            },
                                        ),
                            ) {
                                MarkdownElement(node, components, state.content)
                                if (isHighlighted && highlight?.note != null) {
                                    Icon(
                                        imageVector = CarbonIcons.WarningAlt,
                                        contentDescription = "Has annotation",
                                        tint = Carbon.theme.textSecondary,
                                        modifier = Modifier.size(12.dp).align(Alignment.TopEnd),
                                    )
                                }
                            }
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

private fun buildMarkdownTypography(
    fontScale: Float,
    lineScale: Float,
    heading04: TextStyle,
    heading03: TextStyle,
    headingCompact01: TextStyle,
    bodyCompact01: TextStyle,
    body01: TextStyle,
): DefaultMarkdownTypography {
    fun TextStyle.scaled(): TextStyle =
        copy(
            fontSize = fontSize * fontScale,
            lineHeight = if (lineHeight.isSp) lineHeight * lineScale else lineHeight,
        )

    val scaledBody = body01.scaled()
    return DefaultMarkdownTypography(
        h1 = heading04.scaled(),
        h2 = heading03.scaled(),
        h3 = headingCompact01.scaled(),
        h4 = bodyCompact01.copy(fontWeight = FontWeight.Bold).scaled(),
        h5 = bodyCompact01.copy(fontWeight = FontWeight.Medium).scaled(),
        h6 = bodyCompact01.scaled(),
        paragraph = scaledBody,
        text = scaledBody,
        quote = scaledBody.copy(fontStyle = FontStyle.Italic),
        code = scaledBody.copy(fontFamily = FontFamily.Monospace),
        inlineCode = scaledBody.copy(fontFamily = FontFamily.Monospace),
        bullet = scaledBody,
        list = scaledBody,
        ordered = scaledBody,
        textLink = TextLinkStyles(),
        table = scaledBody,
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
