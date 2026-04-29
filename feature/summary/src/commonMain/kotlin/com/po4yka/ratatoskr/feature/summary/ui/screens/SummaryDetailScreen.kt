@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.ratatoskr.feature.summary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import androidx.compose.material3.LinearProgressIndicator
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.po4yka.ratatoskr.domain.model.AudioStatus
import com.po4yka.ratatoskr.domain.model.FeedbackRating
import com.po4yka.ratatoskr.domain.model.Highlight
import com.po4yka.ratatoskr.domain.model.HighlightColor
import com.po4yka.ratatoskr.domain.model.ProcessingStage
import com.po4yka.ratatoskr.domain.model.ReadingPreferences
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.presentation.navigation.SummaryDetailComponent
import com.po4yka.ratatoskr.core.ui.components.AppSpinner
import com.po4yka.ratatoskr.core.ui.components.AddToCollectionDialog
import com.po4yka.ratatoskr.core.ui.components.AnnotationDialog
import com.po4yka.ratatoskr.core.ui.components.AppIconButton
import com.po4yka.ratatoskr.core.ui.components.FeedbackDialog
import com.po4yka.ratatoskr.core.ui.components.ErrorView
import com.po4yka.ratatoskr.core.ui.components.HeaderIconButton
import com.po4yka.ratatoskr.core.ui.components.LocalImageUrlTransformer
import com.po4yka.ratatoskr.core.ui.components.ProxiedImage
import com.po4yka.ratatoskr.core.ui.components.ProxiedImageTransformer
import com.po4yka.ratatoskr.core.ui.components.ReadingSettingsPanel
import com.po4yka.ratatoskr.core.ui.components.ResummarizeConfirmDialog
import com.po4yka.ratatoskr.core.ui.components.ScreenHeader
import com.po4yka.ratatoskr.core.ui.components.TagChip
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.HighlightBlue
import com.po4yka.ratatoskr.core.ui.theme.HighlightGreen
import com.po4yka.ratatoskr.core.ui.theme.HighlightPink
import com.po4yka.ratatoskr.core.ui.theme.HighlightYellow
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.util.extractDomain
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.audio_error
import ratatoskr.core.ui.generated.resources.audio_generating
import ratatoskr.core.ui.generated.resources.audio_listen
import ratatoskr.core.ui.generated.resources.audio_loading
import ratatoskr.core.ui.generated.resources.audio_pause_narration
import ratatoskr.core.ui.generated.resources.audio_paused
import ratatoskr.core.ui.generated.resources.audio_play_narration
import ratatoskr.core.ui.generated.resources.audio_playing
import ratatoskr.core.ui.generated.resources.audio_stop_narration
import ratatoskr.core.ui.generated.resources.custom_digest_create_read_time
import ratatoskr.core.ui.generated.resources.processing_stage_done
import ratatoskr.core.ui.generated.resources.processing_stage_extraction
import ratatoskr.core.ui.generated.resources.processing_stage_queued
import ratatoskr.core.ui.generated.resources.processing_stage_saving
import ratatoskr.core.ui.generated.resources.processing_stage_summarization
import ratatoskr.core.ui.generated.resources.summary_detail_add_to_collection
import ratatoskr.core.ui.generated.resources.summary_detail_date_format
import ratatoskr.core.ui.generated.resources.summary_detail_exit_highlight_mode
import ratatoskr.core.ui.generated.resources.summary_detail_favorite
import ratatoskr.core.ui.generated.resources.summary_detail_has_annotation
import ratatoskr.core.ui.generated.resources.summary_detail_highlight_mode
import ratatoskr.core.ui.generated.resources.summary_detail_mark_read
import ratatoskr.core.ui.generated.resources.summary_detail_mark_unread
import ratatoskr.core.ui.generated.resources.summary_detail_no_content
import ratatoskr.core.ui.generated.resources.summary_detail_original_article
import ratatoskr.core.ui.generated.resources.summary_detail_re_summarize
import ratatoskr.core.ui.generated.resources.summary_detail_re_summarizing
import ratatoskr.core.ui.generated.resources.summary_detail_reading_offline
import ratatoskr.core.ui.generated.resources.summary_detail_reading_settings
import ratatoskr.core.ui.generated.resources.summary_detail_share
import ratatoskr.core.ui.generated.resources.summary_detail_summary
import ratatoskr.core.ui.generated.resources.summary_detail_thumbs_down
import ratatoskr.core.ui.generated.resources.summary_detail_thumbs_up
import ratatoskr.core.ui.generated.resources.summary_detail_unfavorite
import ratatoskr.core.ui.generated.resources.summary_detail_unknown_source
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource

/** Summary detail screen. */
@Suppress("FunctionNaming")
@Composable
fun SummaryDetailScreen(
    component: SummaryDetailComponent,
    modifier: Modifier = Modifier,
) {
    val viewModel = component.viewModel
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
    ) {
        // Header
        SummaryDetailHeader(
            summary = state.summary,
            onBackClick = component::onBackClicked,
            onShareClick = { viewModel.exportSummary() },
            onFavoriteClick = { viewModel.toggleFavorite() },
            onAddToCollectionClick = { viewModel.showAddToCollection() },
            onReadingSettingsClick = { viewModel.toggleReadingSettings() },
            onHighlightModeClick = { viewModel.toggleHighlightMode() },
            isHighlightModeActive = state.highlights.isHighlightModeActive,
            onThumbsUpClick = { viewModel.rateSummary(FeedbackRating.UP) },
            onThumbsDownClick = { viewModel.rateSummary(FeedbackRating.DOWN) },
            feedbackRating = state.feedback.feedback?.rating,
            onResummarizeClick = { viewModel.openResummarizeConfirmDialog() },
            isResummarizing = state.feedback.isResummarizing,
        )

        // Offline banner (Feature 3.2)
        if (state.isOffline) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Polite }
                        .background(AppTheme.colors.layer02)
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    imageVector = AppIcons.WifiOff,
                    contentDescription = null,
                    tint = AppTheme.colors.supportWarning,
                    modifier = Modifier.size(IconSizes.sm),
                )
                Text(
                    text = stringResource(Res.string.summary_detail_reading_offline),
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                )
            }
        }

        // Export error banner
        state.exportError?.let { exportError ->
            Text(
                text = exportError,
                style = AppTheme.type.label01,
                color = AppTheme.colors.supportError,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            )
        }

        // Re-summarize progress indicator
        if (state.feedback.isResummarizing) {
            val reSummarizeDesc = stringResource(Res.string.summary_detail_re_summarizing)
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            ) {
                LinearProgressIndicator(
                    progress = { state.feedback.resummarizeProgress },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = reSummarizeDesc },
                )
                Text(
                    text =
                        if (state.feedback.resummarizeStage == ProcessingStage.UNSPECIFIED) {
                            stringResource(Res.string.summary_detail_re_summarizing)
                        } else {
                            processingStageLabel(state.feedback.resummarizeStage)
                        },
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.textSecondary,
                    modifier = Modifier.padding(top = Spacing.xs),
                )
            }
        }

        // Re-summarize error
        state.feedback.resummarizeError?.let { errorMessage ->
            Text(
                text = errorMessage,
                style = AppTheme.type.label01,
                color = AppTheme.colors.supportError,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            )
        }

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
                    onRetry = { viewModel.loadSummary(component.summaryId) },
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
                    AppSpinner(modifier = Modifier.size(88.dp))
                }
            }

            state.summary != null -> {
                SummaryDetailContent(
                    summary = state.summary!!,
                    readingPreferences = state.readingPreferences,
                    initialScrollPosition = state.lastReadPosition,
                    initialScrollOffset = state.lastReadOffset,
                    highlights = state.highlights.highlights,
                    highlightedNodeOffsets = state.highlights.highlightedNodeOffsets,
                    isHighlightModeActive = state.highlights.isHighlightModeActive,
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

        // Audio player row (Feature 3.3)
        AudioPlayerRow(
            audioState = state.audioState,
            onPlayPause = {
                if (state.audioState == null) {
                    viewModel.generateAndPlayAudio()
                } else {
                    viewModel.toggleAudioPlayback()
                }
            },
            onStop = { viewModel.stopAudio() },
        )
    }

    // Add to Collection Dialog
    if (state.collection.showDialog) {
        AddToCollectionDialog(
            collections = state.collection.collections,
            isLoading = state.collection.isLoading,
            isAdding = state.collection.isAdding,
            error = state.collection.error,
            onCollectionSelected = { id -> viewModel.addToCollection(id) },
            onDismiss = { viewModel.dismissAddToCollection() },
        )
    }

    // Annotation Dialog
    if (state.highlights.editingAnnotationHighlightId != null) {
        AnnotationDialog(
            draft = state.highlights.annotationDraft,
            onDraftChange = { viewModel.updateAnnotationDraft(it) },
            onSave = { viewModel.saveAnnotation() },
            onCancel = { viewModel.closeAnnotationEditor() },
        )
    }

    // Feedback Dialog
    if (state.feedback.showFeedbackDialog) {
        FeedbackDialog(
            rating = FeedbackRating.DOWN,  // explicit, will be easy to update later
            isSubmitting = state.feedback.isSubmittingFeedback,
            onSubmit = { rating, issues, comment ->
                viewModel.submitDetailedFeedback(rating, issues, comment)
            },
            onDismiss = { viewModel.dismissFeedbackDialog() },
        )
    }

    // Re-summarize Confirm Dialog
    if (state.feedback.showResummarizeConfirmDialog) {
        ResummarizeConfirmDialog(
            onConfirm = { viewModel.resummarize() },
            onDismiss = { viewModel.dismissResummarizeConfirmDialog() },
        )
    }
}

@Composable
private fun processingStageLabel(stage: ProcessingStage): String =
    when (stage) {
        ProcessingStage.UNSPECIFIED -> stringResource(Res.string.summary_detail_re_summarizing)
        ProcessingStage.QUEUED -> stringResource(Res.string.processing_stage_queued)
        ProcessingStage.EXTRACTION -> stringResource(Res.string.processing_stage_extraction)
        ProcessingStage.SUMMARIZATION -> stringResource(Res.string.processing_stage_summarization)
        ProcessingStage.SAVING -> stringResource(Res.string.processing_stage_saving)
        ProcessingStage.DONE -> stringResource(Res.string.processing_stage_done)
    }

@Suppress("FunctionNaming")
@Composable
private fun AudioPlayerRow(
    audioState: com.po4yka.ratatoskr.domain.model.AudioPlaybackState?,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
) {
    val status = audioState?.status ?: AudioStatus.IDLE
    val isLoading = status == AudioStatus.GENERATING || status == AudioStatus.LOADING
    val isPlaying = status == AudioStatus.PLAYING
    val isActive = audioState != null && status != AudioStatus.ERROR

    HorizontalDivider(color = AppTheme.colors.borderSubtle00)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.layer01)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (isLoading) {
            val audioLoadingDesc =
                if (status == AudioStatus.GENERATING) {
                    stringResource(Res.string.audio_generating)
                } else {
                    stringResource(Res.string.audio_loading)
                }
            AppSpinner(
                modifier = Modifier.size(IconSizes.sm).semantics { contentDescription = audioLoadingDesc },
            )
        } else {
            val playPauseDesc =
                if (isPlaying) {
                    stringResource(Res.string.audio_pause_narration)
                } else {
                    stringResource(Res.string.audio_play_narration)
                }
            AppIconButton(
                imageVector = if (isPlaying) AppIcons.PauseFilled else AppIcons.PlayFilled,
                contentDescription = playPauseDesc,
                onClick = onPlayPause,
                buttonSize = IconSizes.sm,
                iconSize = IconSizes.sm,
            )
        }

        val generatingText = stringResource(Res.string.audio_generating)
        val loadingText = stringResource(Res.string.audio_loading)
        val playingText = stringResource(Res.string.audio_playing)
        val pausedText = stringResource(Res.string.audio_paused)
        val audioErrorText = stringResource(Res.string.audio_error)
        val listenText = stringResource(Res.string.audio_listen)
        val audioLabel =
            when (status) {
                AudioStatus.GENERATING -> generatingText
                AudioStatus.LOADING -> loadingText
                AudioStatus.PLAYING -> playingText
                AudioStatus.PAUSED -> pausedText
                AudioStatus.ERROR -> audioState?.error ?: audioErrorText
                AudioStatus.IDLE -> listenText
            }
        Text(
            text = audioLabel,
            style = AppTheme.type.label01,
            color = if (status == AudioStatus.ERROR) AppTheme.colors.supportError else AppTheme.colors.textSecondary,
            modifier = Modifier.weight(1f),
        )

        if (isActive && !isLoading) {
            AppIconButton(
                imageVector = AppIcons.Close,
                contentDescription = stringResource(Res.string.audio_stop_narration),
                onClick = onStop,
                tint = AppTheme.colors.iconSecondary,
                buttonSize = IconSizes.sm,
                iconSize = IconSizes.sm,
            )
        }
    }
}

@Suppress("FunctionNaming", "LongParameterList")
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
    onThumbsUpClick: () -> Unit = {},
    onThumbsDownClick: () -> Unit = {},
    feedbackRating: FeedbackRating? = null,
    onResummarizeClick: () -> Unit = {},
    isResummarizing: Boolean = false,
) {
    val summaryTitle =
        summary?.let { extractDomain(it.sourceUrl) ?: stringResource(Res.string.summary_detail_summary) }
            ?: stringResource(Res.string.summary_detail_summary)
    ScreenHeader(
        title = summaryTitle,
        isDetailScreen = true,
        onBackClick = onBackClick,
        actions = {
            summary?.let { s ->
                val favoriteDesc =
                    if (s.isFavorited) {
                        stringResource(Res.string.summary_detail_unfavorite)
                    } else {
                        stringResource(Res.string.summary_detail_favorite)
                    }
                HeaderIconButton(
                    icon =
                        if (s.isFavorited) {
                            AppIcons.FavoriteFilled
                        } else {
                            AppIcons.Favorite
                        },
                    contentDescription = favoriteDesc,
                    onClick = onFavoriteClick,
                    tint =
                        if (s.isFavorited) {
                            AppTheme.colors.supportError
                        } else {
                            AppTheme.colors.iconSecondary
                        },
                )

                HeaderIconButton(
                    icon = AppIcons.ThumbsUp,
                    contentDescription = stringResource(Res.string.summary_detail_thumbs_up),
                    onClick = onThumbsUpClick,
                    tint =
                        if (feedbackRating == FeedbackRating.UP) {
                            AppTheme.colors.supportSuccess
                        } else {
                            AppTheme.colors.iconSecondary
                        },
                )

                HeaderIconButton(
                    icon = AppIcons.ThumbsDown,
                    contentDescription = stringResource(Res.string.summary_detail_thumbs_down),
                    onClick = onThumbsDownClick,
                    tint =
                        if (feedbackRating == FeedbackRating.DOWN) {
                            AppTheme.colors.supportError
                        } else {
                            AppTheme.colors.iconSecondary
                        },
                )

                HeaderIconButton(
                    icon = AppIcons.Folder,
                    contentDescription = stringResource(Res.string.summary_detail_add_to_collection),
                    onClick = onAddToCollectionClick,
                )

                val highlightDesc =
                    if (isHighlightModeActive) {
                        stringResource(Res.string.summary_detail_exit_highlight_mode)
                    } else {
                        stringResource(Res.string.summary_detail_highlight_mode)
                    }
                HeaderIconButton(
                    icon = if (isHighlightModeActive) AppIcons.BookmarkAdd else AppIcons.Bookmark,
                    contentDescription = highlightDesc,
                    onClick = onHighlightModeClick,
                    tint = if (isHighlightModeActive) AppTheme.colors.linkPrimary else AppTheme.colors.iconSecondary,
                )

                val readDesc =
                    if (s.isRead) {
                        stringResource(Res.string.summary_detail_mark_read)
                    } else {
                        stringResource(Res.string.summary_detail_mark_unread)
                    }
                Icon(
                    imageVector = if (s.isRead) AppIcons.CheckmarkFilled else AppIcons.CircleOutline,
                    contentDescription = readDesc,
                    tint = if (s.isRead) AppTheme.colors.supportSuccess else AppTheme.colors.iconSecondary,
                    modifier = Modifier.size(IconSizes.sm),
                )

                HeaderIconButton(
                    icon = AppIcons.Settings,
                    contentDescription = stringResource(Res.string.summary_detail_reading_settings),
                    onClick = onReadingSettingsClick,
                )

                HeaderIconButton(
                    icon = AppIcons.Renew,
                    contentDescription = stringResource(Res.string.summary_detail_re_summarize),
                    onClick = { if (!isResummarizing) onResummarizeClick() },
                    tint = AppTheme.colors.iconSecondary,
                    modifier = Modifier.alpha(if (isResummarizing) 0.4f else 1f),
                )

                HeaderIconButton(
                    icon = AppIcons.Share,
                    contentDescription = stringResource(Res.string.summary_detail_share),
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

    val imageUrlTransformer = LocalImageUrlTransformer.current
    val imageTransformer = remember(imageUrlTransformer) { ProxiedImageTransformer(imageUrlTransformer) }

    Column(modifier = modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { readingProgress },
            modifier = Modifier.fillMaxWidth().height(2.dp),
        )

        val textPrimary = AppTheme.colors.textPrimary
        val layer01 = AppTheme.colors.layer01
        val borderSubtle00 = AppTheme.colors.borderSubtle00
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

        val heading04 = AppTheme.type.heading04
        val heading03 = AppTheme.type.heading03
        val headingCompact01 = AppTheme.type.headingCompact01
        val bodyCompact01 = AppTheme.type.bodyCompact01
        val body01 = AppTheme.type.body01
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
                    text = stringResource(Res.string.summary_detail_no_content),
                    style = AppTheme.type.body01,
                    color = AppTheme.colors.textSecondary,
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
                        AppSpinner(modifier = Modifier.size(48.dp))
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
                            HorizontalDivider(color = AppTheme.colors.borderSubtle00)
                            Spacer(modifier = Modifier.height(Spacing.md))
                        }
                        item(key = "fallback_text") {
                            Text(
                                text = markdownContent,
                                style = AppTheme.type.body01,
                                color = AppTheme.colors.textPrimary,
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
                            HorizontalDivider(color = AppTheme.colors.borderSubtle00)
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
                                        imageVector = AppIcons.WarningAlt,
                                        contentDescription = stringResource(Res.string.summary_detail_has_annotation),
                                        tint = AppTheme.colors.textSecondary,
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
            style = AppTheme.type.heading04,
            color = AppTheme.colors.textPrimary,
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
            text = extractDomain(summary.sourceUrl) ?: stringResource(Res.string.summary_detail_unknown_source),
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
        )
        val createdAtLabel = formatDate(summary.createdAt)
        val readTimeLabel =
            summary.readingTimeMin?.let {
                stringResource(Res.string.custom_digest_create_read_time, it)
            }
        Text(
            text =
                buildString {
                    append(createdAtLabel)
                    readTimeLabel?.let {
                        append(" | ")
                        append(it)
                    }
                },
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
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
        HorizontalDivider(color = AppTheme.colors.borderSubtle00)
        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = stringResource(Res.string.summary_detail_original_article),
            style = AppTheme.type.headingCompact01,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(Spacing.xs))

        val uriHandler = LocalUriHandler.current
        Text(
            text = sourceUrl,
            style = AppTheme.type.label01,
            color = AppTheme.colors.linkPrimary,
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

@Composable
private fun formatDate(instant: Instant): String {
    val civilDate = civilFromDays((instant.epochSeconds / 86400).toInt())
    return stringResource(
        Res.string.summary_detail_date_format,
        civilDate.month,
        civilDate.day,
        civilDate.year,
    )
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
