package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.domain.model.AudioPlaybackState
import com.po4yka.bitesizereader.domain.model.AudioStatus
import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.domain.repository.ReadingPreferencesRepository
import com.po4yka.bitesizereader.domain.usecase.AddToCollectionUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.EndReadingSessionUseCase
import com.po4yka.bitesizereader.domain.usecase.GenerateAudioUseCase
import com.po4yka.bitesizereader.domain.usecase.GetAudioUseCase
import com.po4yka.bitesizereader.domain.usecase.GetHighlightsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryContentUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryFeedbackUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.RefreshFullContentUseCase
import com.po4yka.bitesizereader.domain.usecase.SaveReadPositionUseCase
import com.po4yka.bitesizereader.domain.usecase.StartReadingSessionUseCase
import com.po4yka.bitesizereader.domain.usecase.SubmitSummaryFeedbackUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleHighlightUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateAnnotationUseCase
import com.po4yka.bitesizereader.util.audio.AudioPlayer
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SummaryDetailViewModel(
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
    private val getSummaryContentUseCase: GetSummaryContentUseCase,
    private val refreshFullContentUseCase: RefreshFullContentUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val saveReadPositionUseCase: SaveReadPositionUseCase,
    private val readingPreferencesRepository: ReadingPreferencesRepository,
    private val collectionRepository: CollectionRepository,
    private val addToCollectionUseCase: AddToCollectionUseCase,
    private val generateAudioUseCase: GenerateAudioUseCase,
    private val getAudioUseCase: GetAudioUseCase,
    private val audioPlayer: AudioPlayer,
    private val startReadingSessionUseCase: StartReadingSessionUseCase,
    private val endReadingSessionUseCase: EndReadingSessionUseCase,
    private val getHighlightsUseCase: GetHighlightsUseCase,
    private val toggleHighlightUseCase: ToggleHighlightUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase,
    private val submitSummaryFeedbackUseCase: SubmitSummaryFeedbackUseCase,
    private val getSummaryFeedbackUseCase: GetSummaryFeedbackUseCase,
    private val processingService: ProcessingService,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    private var activeSessionId: Long? = null
    private var sessionStartTime: Instant = Clock.System.now()
    private var lastScrollTime: Instant = Clock.System.now()
    private var isSessionPaused: Boolean = false

    companion object {
        private val INACTIVITY_THRESHOLD_MS = 5 * 60 * 1000L
    }

    init {
        readingPreferencesRepository.getPreferences()
            .onEach { prefs ->
                _state.value = _state.value.copy(readingPreferences = prefs)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                checkInactivity()
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadSummary(id: String) {
        viewModelScope.launch {
            _state.value = SummaryDetailState(isLoading = true)
            try {
                val summary = getSummaryByIdUseCase(id)
                _state.value =
                    _state.value.copy(
                        summary = summary,
                        isLoading = false,
                        lastReadPosition = summary?.lastReadPosition ?: 0,
                        lastReadOffset = summary?.lastReadOffset ?: 0,
                    )
                if (summary != null && !summary.isRead) {
                    markSummaryAsReadUseCase(id)
                }
                if (summary != null) {
                    if (activeSessionId != null) {
                        endReadingSession()
                    }
                    activeSessionId = startReadingSessionUseCase(id)
                    val now = Clock.System.now()
                    sessionStartTime = now
                    lastScrollTime = now
                    isSessionPaused = false
                    fetchFullContent(id)
                    viewModelScope.launch {
                        getHighlightsUseCase(id).collect { highlights ->
                            _state.update { state ->
                                state.copy(
                                    highlights = highlights,
                                    highlightedNodeOffsets = highlights.map { it.nodeOffset }.toSet(),
                                )
                            }
                        }
                    }
                    getSummaryFeedbackUseCase(id)
                        .onEach { feedback -> _state.update { it.copy(feedback = feedback) } }
                        .launchIn(viewModelScope)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun fetchFullContent(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingContent = true)
            try {
                val fullContent = getSummaryContentUseCase(id)
                if (fullContent != null) {
                    _state.value =
                        _state.value.copy(
                            summary =
                                _state.value.summary?.copy(
                                    fullContent = fullContent,
                                    isFullContentCached = true,
                                ),
                            isLoadingContent = false,
                        )
                    // Stale-while-revalidate: refresh in background if cache is stale
                    viewModelScope.launch {
                        try {
                            val refreshed = refreshFullContentUseCase(id)
                            if (refreshed != null) {
                                _state.value =
                                    _state.value.copy(
                                        summary = _state.value.summary?.copy(fullContent = refreshed),
                                    )
                            }
                        } catch (e: Exception) {
                            logger.debug(e) { "Background content refresh failed for $id" }
                        }
                    }
                } else {
                    _state.value = _state.value.copy(isLoadingContent = false)
                }
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        isLoadingContent = false,
                        error = e.toAppError().userMessage(),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun toggleFavorite() {
        val summary = _state.value.summary ?: return
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(summary.id)
                _state.value =
                    _state.value.copy(
                        summary = summary.copy(isFavorited = !summary.isFavorited),
                    )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.toAppError().userMessage())
            }
        }
    }

    fun toggleReadingSettings() {
        _state.value = _state.value.copy(showReadingSettings = !_state.value.showReadingSettings)
    }

    fun updateFontSizeScale(scale: Float) {
        viewModelScope.launch {
            readingPreferencesRepository.updateFontSizeScale(scale)
        }
    }

    fun updateLineSpacingScale(scale: Float) {
        viewModelScope.launch {
            readingPreferencesRepository.updateLineSpacingScale(scale)
        }
    }

    fun notifyScrolled() {
        val now = Clock.System.now()
        lastScrollTime = now
        if (isSessionPaused) {
            isSessionPaused = false
            sessionStartTime = now
        }
    }

    private fun checkInactivity() {
        val now = Clock.System.now()
        if (!isSessionPaused && activeSessionId != null &&
            (now - lastScrollTime).inWholeMilliseconds > INACTIVITY_THRESHOLD_MS
        ) {
            isSessionPaused = true
        }
    }

    private fun endReadingSession() {
        val sessionId = activeSessionId ?: return
        activeSessionId = null
        val now = Clock.System.now()
        val durationSec =
            if (isSessionPaused) {
                (lastScrollTime - sessionStartTime).inWholeSeconds.toInt().coerceAtLeast(0)
            } else {
                (now - sessionStartTime).inWholeSeconds.toInt().coerceAtLeast(0)
            }
        viewModelScope.launch {
            try {
                endReadingSessionUseCase(sessionId, durationSec)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.debug(e) { "Failed to end reading session $sessionId" }
            }
        }
    }

    fun saveReadPosition(
        position: Int,
        offset: Int,
    ) {
        val summaryId = _state.value.summary?.id ?: return
        endReadingSession()
        viewModelScope.launch {
            try {
                saveReadPositionUseCase(summaryId, position, offset)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.debug(e) { "Failed to save read position for $summaryId" }
            }
        }
    }

    @Suppress("unused", "TooGenericExceptionCaught")
    fun deleteSummary(id: String) {
        viewModelScope.launch {
            try {
                deleteSummaryUseCase(id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.toAppError().userMessage())
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun showAddToCollection() {
        _state.value =
            _state.value.copy(
                showAddToCollectionDialog = true,
                isLoadingCollections = true,
                addToCollectionError = null,
            )
        viewModelScope.launch {
            try {
                val collections = collectionRepository.getCollections().first()
                _state.value =
                    _state.value.copy(
                        collections = collections,
                        isLoadingCollections = false,
                    )
            } catch (e: Exception) {
                logger.warn(e) { "Failed to load collections" }
                _state.value =
                    _state.value.copy(
                        isLoadingCollections = false,
                        addToCollectionError = e.toAppError().userMessage(),
                    )
            }
        }
    }

    fun dismissAddToCollection() {
        _state.value =
            _state.value.copy(
                showAddToCollectionDialog = false,
                addToCollectionError = null,
            )
    }

    @Suppress("TooGenericExceptionCaught")
    fun generateAndPlayAudio(sourceField: String = "summary_1000") {
        val summaryId = _state.value.summary?.id?.toLongOrNull() ?: return
        _state.value =
            _state.value.copy(
                audioState = AudioPlaybackState(summaryId = summaryId, status = AudioStatus.GENERATING),
            )
        viewModelScope.launch {
            try {
                val genResult = generateAudioUseCase(summaryId, sourceField)
                genResult.getOrThrow()
                _state.value =
                    _state.value.copy(
                        audioState = _state.value.audioState?.copy(status = AudioStatus.LOADING),
                    )
                val audioBytes = getAudioUseCase(summaryId).getOrThrow()
                audioPlayer.playFromBytes(audioBytes)
                _state.value =
                    _state.value.copy(
                        audioState =
                            _state.value.audioState?.copy(
                                status = AudioStatus.PLAYING,
                                durationMs = audioPlayer.durationMs,
                            ),
                    )
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        audioState =
                            _state.value.audioState?.copy(
                                status = AudioStatus.ERROR,
                                error = e.toAppError().userMessage(),
                            ),
                    )
            }
        }
    }

    fun toggleAudioPlayback() {
        val audio = _state.value.audioState ?: return
        when (audio.status) {
            AudioStatus.PLAYING -> {
                audioPlayer.pause()
                _state.value =
                    _state.value.copy(
                        audioState = audio.copy(status = AudioStatus.PAUSED),
                    )
            }
            AudioStatus.PAUSED -> {
                audioPlayer.resume()
                _state.value =
                    _state.value.copy(
                        audioState = audio.copy(status = AudioStatus.PLAYING),
                    )
            }
            else -> { /* no-op for other states */ }
        }
    }

    fun stopAudio() {
        audioPlayer.stop()
        _state.value = _state.value.copy(audioState = null)
    }

    fun toggleHighlightMode() {
        _state.update { it.copy(isHighlightModeActive = !it.isHighlightModeActive) }
    }

    fun toggleHighlight(
        nodeOffset: Int,
        text: String,
    ) {
        val summaryId = _state.value.summary?.id ?: return
        viewModelScope.launch {
            toggleHighlightUseCase(
                summaryId = summaryId,
                nodeOffset = nodeOffset,
                text = text,
                existingHighlights = _state.value.highlights,
            )
        }
    }

    fun openAnnotationEditor(highlightId: String) {
        val note = _state.value.highlights.find { it.id == highlightId }?.note ?: ""
        _state.update { it.copy(editingAnnotationHighlightId = highlightId, annotationDraft = note) }
    }

    fun updateAnnotationDraft(text: String) {
        _state.update { it.copy(annotationDraft = text) }
    }

    fun saveAnnotation() {
        val highlightId = _state.value.editingAnnotationHighlightId ?: return
        val note = _state.value.annotationDraft.trim().ifEmpty { null }
        viewModelScope.launch {
            updateAnnotationUseCase(highlightId, note)
            _state.update { it.copy(editingAnnotationHighlightId = null, annotationDraft = "") }
        }
    }

    fun closeAnnotationEditor() {
        _state.update { it.copy(editingAnnotationHighlightId = null, annotationDraft = "") }
    }

    fun rateSummary(rating: FeedbackRating) {
        val summaryId = _state.value.summary?.id ?: return
        if (rating == FeedbackRating.UP) {
            viewModelScope.launch {
                try {
                    submitSummaryFeedbackUseCase(summaryId, rating, emptyList(), null)
                } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                    logger.warn(e) { "Failed to submit thumbs up feedback for $summaryId" }
                }
            }
        } else {
            _state.update { it.copy(showFeedbackDialog = true) }
        }
    }

    fun dismissFeedbackDialog() {
        _state.update { it.copy(showFeedbackDialog = false) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun submitDetailedFeedback(
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    ) {
        val summaryId = _state.value.summary?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingFeedback = true) }
            try {
                submitSummaryFeedbackUseCase(summaryId, rating, issues, comment)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to submit detailed feedback for $summaryId" }
            } finally {
                _state.update { it.copy(isSubmittingFeedback = false, showFeedbackDialog = false) }
            }
        }
    }

    fun openResummarizeConfirmDialog() {
        _state.update { it.copy(showResummarizeConfirmDialog = true) }
    }

    fun dismissResummarizeConfirmDialog() {
        _state.update { it.copy(showResummarizeConfirmDialog = false) }
    }

    @Suppress("TooGenericExceptionCaught")
    fun resummarize() {
        if (_state.value.isResummarizing) return
        val sourceUrl = _state.value.summary?.sourceUrl ?: return
        dismissResummarizeConfirmDialog()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isResummarizing = true,
                    resummarizeError = null,
                    resummarizeProgress = 0f,
                    resummarizeStage = ProcessingStage.UNSPECIFIED,
                )
            }
            try {
                processingService.submitUrl(sourceUrl, forceRefresh = true).collect { update ->
                    _state.update { it.copy(resummarizeProgress = update.progress, resummarizeStage = update.stage) }
                    if (update.stage == ProcessingStage.DONE) {
                        loadSummary(_state.value.summary?.id ?: return@collect)
                        _state.update { it.copy(isResummarizing = false) }
                        return@collect
                    }
                }
            } catch (e: Exception) {
                logger.warn { "Re-summarize failed: ${e.message}" }
                _state.update {
                    it.copy(
                        isResummarizing = false,
                        resummarizeError = e.message ?: "Re-summarization failed",
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        endReadingSession()
        super.onDestroy()
    }

    @Suppress("TooGenericExceptionCaught")
    fun addToCollection(collectionId: String) {
        val summaryId = _state.value.summary?.id ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isAddingToCollection = true, addToCollectionError = null)
            try {
                addToCollectionUseCase(collectionId, summaryId)
                _state.value =
                    _state.value.copy(
                        isAddingToCollection = false,
                        showAddToCollectionDialog = false,
                    )
            } catch (e: Exception) {
                logger.warn(e) { "Failed to add to collection" }
                _state.value =
                    _state.value.copy(
                        isAddingToCollection = false,
                        addToCollectionError = e.toAppError().userMessage(),
                    )
            }
        }
    }
}
