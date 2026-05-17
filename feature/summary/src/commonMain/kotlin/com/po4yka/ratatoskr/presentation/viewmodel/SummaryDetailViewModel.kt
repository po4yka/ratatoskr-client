package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.FeedbackIssue
import com.po4yka.ratatoskr.domain.model.FeedbackRating
import com.po4yka.ratatoskr.domain.repository.ReadingPreferencesRepository
import com.po4yka.ratatoskr.domain.usecase.DeleteSummaryUseCase
import com.po4yka.ratatoskr.domain.usecase.ExportSummaryUseCase
import com.po4yka.ratatoskr.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.ratatoskr.domain.usecase.GetSummaryContentUseCase
import com.po4yka.ratatoskr.domain.usecase.RefreshFullContentUseCase
import com.po4yka.ratatoskr.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.ratatoskr.presentation.state.SummaryDetailState
import com.po4yka.ratatoskr.util.error.runCatchingDomain
import com.po4yka.ratatoskr.util.error.toAppError
import com.po4yka.ratatoskr.util.error.userMessage
import com.po4yka.ratatoskr.util.network.NetworkMonitor
import com.po4yka.ratatoskr.util.network.isConnected
import com.po4yka.ratatoskr.util.share.ShareManager
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class SummaryDetailViewModel(
    private val readingSessionDelegate: ReadingSessionDelegate,
    private val audioDelegate: AudioDelegate,
    private val highlightDelegate: HighlightDelegate,
    private val feedbackDelegate: FeedbackDelegate,
    private val collectionDelegate: CollectionDelegate,
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
    private val getSummaryContentUseCase: GetSummaryContentUseCase,
    private val refreshFullContentUseCase: RefreshFullContentUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val readingPreferencesRepository: ReadingPreferencesRepository,
    private val networkMonitor: NetworkMonitor,
    private val exportSummaryUseCase: ExportSummaryUseCase,
    private val shareManager: ShareManager,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    init {
        readingPreferencesRepository.getPreferences()
            .onEach { prefs ->
                _state.update { it.copy(readingPreferences = prefs) }
            }
            .launchIn(viewModelScope)

        networkMonitor.networkStatus
            .onEach { status ->
                _state.update { it.copy(isOffline = !status.isConnected()) }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                readingSessionDelegate.checkInactivity(
                    currentState = { _state.value.session },
                ) { sub -> _state.update { it.copy(session = sub) } }
            }
        }
    }

    fun loadSummary(id: String) {
        viewModelScope.launch {
            _state.value = SummaryDetailState(isLoading = true)
            runCatchingDomain { getSummaryByIdUseCase(id) }
                .onSuccess { summary ->
                    _state.update {
                        it.copy(
                            summary = summary,
                            isLoading = false,
                            lastReadPosition = summary?.lastReadPosition ?: 0,
                            lastReadOffset = summary?.lastReadOffset ?: 0,
                        )
                    }
                    if (summary != null) {
                        readingSessionDelegate.startSession(
                            summaryId = id,
                            isRead = summary.isRead,
                            scope = viewModelScope,
                        ) { sub -> _state.update { it.copy(session = sub) } }
                        fetchFullContent(id)
                        highlightDelegate.observeHighlights(
                            summaryId = id,
                            scope = viewModelScope,
                            currentState = { _state.value.highlights },
                        ) { sub -> _state.update { it.copy(highlights = sub) } }
                        feedbackDelegate.observeFeedback(
                            summaryId = id,
                            scope = viewModelScope,
                            currentState = { _state.value.feedback },
                        ) { sub -> _state.update { it.copy(feedback = sub) } }
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.toAppError().userMessage()) }
                }
        }
    }

    private fun fetchFullContent(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingContent = true) }
            runCatchingDomain { getSummaryContentUseCase(id) }
                .onSuccess { fullContent ->
                    if (fullContent != null) {
                        _state.update {
                            it.copy(
                                summary =
                                    it.summary?.copy(
                                        fullContent = fullContent,
                                        isFullContentCached = true,
                                    ),
                                isLoadingContent = false,
                            )
                        }
                        // Stale-while-revalidate: refresh in background if cache is stale.
                        // The originating summary id is captured here so the inner write
                        // aborts when the user has navigated to a different summary by the
                        // time the network call resolves — preventing a cross-summary write.
                        viewModelScope.launch {
                            runCatchingDomain { refreshFullContentUseCase(id) }
                                .onSuccess { refreshed ->
                                    if (refreshed != null) {
                                        _state.update { current ->
                                            if (current.summary?.id != id) {
                                                current
                                            } else {
                                                current.copy(
                                                    summary = current.summary.copy(fullContent = refreshed),
                                                )
                                            }
                                        }
                                    }
                                }
                                .onFailure { e ->
                                    logger.debug(e) { "Background content refresh failed for $id" }
                                }
                        }
                    } else {
                        _state.update { it.copy(isLoadingContent = false) }
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoadingContent = false,
                            error = e.toAppError().userMessage(),
                        )
                    }
                }
        }
    }

    fun toggleFavorite() {
        val summary = _state.value.summary ?: return
        viewModelScope.launch {
            runCatchingDomain { toggleFavoriteUseCase(summary.id) }
                .onSuccess {
                    _state.update {
                        it.copy(
                            summary = summary.copy(isFavorited = !summary.isFavorited),
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.toAppError().userMessage()) }
                }
        }
    }

    fun toggleReadingSettings() {
        _state.update { it.copy(showReadingSettings = !it.showReadingSettings) }
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
        readingSessionDelegate.notifyScrolled(
            currentState = { _state.value.session },
        ) { sub -> _state.update { it.copy(session = sub) } }
    }

    fun saveReadPosition(
        position: Int,
        offset: Int,
    ) {
        val summaryId = _state.value.summary?.id ?: return
        readingSessionDelegate.endSession(
            scope = viewModelScope,
            currentState = { _state.value.session },
        ) { sub -> _state.update { it.copy(session = sub) } }
        readingSessionDelegate.saveReadPosition(summaryId, position, offset, viewModelScope)
    }

    @Suppress("unused") // Public API for UI layer
    fun deleteSummary(id: String) {
        viewModelScope.launch {
            runCatchingDomain { deleteSummaryUseCase(id) }
                .onFailure { e ->
                    _state.update { it.copy(error = e.toAppError().userMessage()) }
                }
        }
    }

    // Audio

    fun generateAndPlayAudio(sourceField: String = "summary_1000") {
        val summaryId = _state.value.summary?.id ?: return
        audioDelegate.generateAndPlayAudio(
            summaryId = summaryId,
            sourceField = sourceField,
            scope = viewModelScope,
            currentState = { _state.value.audioState },
        ) { audio -> _state.update { it.copy(audioState = audio) } }
    }

    fun toggleAudioPlayback() {
        audioDelegate.toggleAudioPlayback(
            currentState = { _state.value.audioState },
        ) { audio -> _state.update { it.copy(audioState = audio) } }
    }

    fun stopAudio() {
        audioDelegate.stopAudio { audio -> _state.update { it.copy(audioState = audio) } }
    }

    // Highlights

    fun toggleHighlightMode() {
        highlightDelegate.toggleHighlightMode(
            currentState = { _state.value.highlights },
        ) { sub -> _state.update { it.copy(highlights = sub) } }
    }

    fun toggleHighlight(
        nodeOffset: Int,
        text: String,
    ) {
        val summaryId = _state.value.summary?.id ?: return
        highlightDelegate.toggleHighlight(
            summaryId = summaryId,
            nodeOffset = nodeOffset,
            text = text,
            currentState = { _state.value.highlights },
            scope = viewModelScope,
        )
    }

    fun openAnnotationEditor(highlightId: String) {
        highlightDelegate.openAnnotationEditor(
            highlightId = highlightId,
            currentState = { _state.value.highlights },
        ) { sub -> _state.update { it.copy(highlights = sub) } }
    }

    fun updateAnnotationDraft(text: String) {
        highlightDelegate.updateAnnotationDraft(
            text = text,
            currentState = { _state.value.highlights },
        ) { sub -> _state.update { it.copy(highlights = sub) } }
    }

    fun saveAnnotation() {
        highlightDelegate.saveAnnotation(
            scope = viewModelScope,
            currentState = { _state.value.highlights },
        ) { sub -> _state.update { it.copy(highlights = sub) } }
    }

    fun closeAnnotationEditor() {
        highlightDelegate.closeAnnotationEditor(
            currentState = { _state.value.highlights },
        ) { sub -> _state.update { it.copy(highlights = sub) } }
    }

    // Feedback

    fun rateSummary(rating: FeedbackRating) {
        val summaryId = _state.value.summary?.id ?: return
        feedbackDelegate.rateSummary(
            summaryId = summaryId,
            rating = rating,
            scope = viewModelScope,
            currentState = { _state.value.feedback },
        ) { sub -> _state.update { it.copy(feedback = sub) } }
    }

    fun dismissFeedbackDialog() {
        feedbackDelegate.dismissFeedbackDialog(
            currentState = { _state.value.feedback },
        ) { sub -> _state.update { it.copy(feedback = sub) } }
    }

    fun submitDetailedFeedback(
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    ) {
        val summaryId = _state.value.summary?.id ?: return
        feedbackDelegate.submitDetailedFeedback(
            summaryId = summaryId,
            rating = rating,
            issues = issues,
            comment = comment,
            scope = viewModelScope,
            currentState = { _state.value.feedback },
        ) { sub -> _state.update { it.copy(feedback = sub) } }
    }

    fun openResummarizeConfirmDialog() {
        feedbackDelegate.openResummarizeConfirmDialog(
            currentState = { _state.value.feedback },
        ) { sub -> _state.update { it.copy(feedback = sub) } }
    }

    fun dismissResummarizeConfirmDialog() {
        feedbackDelegate.dismissResummarizeConfirmDialog(
            currentState = { _state.value.feedback },
        ) { sub -> _state.update { it.copy(feedback = sub) } }
    }

    fun resummarize() {
        val sourceUrl = _state.value.summary?.sourceUrl ?: return
        val summaryId = _state.value.summary?.id ?: return
        feedbackDelegate.resummarize(
            sourceUrl = sourceUrl,
            scope = viewModelScope,
            currentState = { _state.value.feedback },
            onState = { sub -> _state.update { it.copy(feedback = sub) } },
            onSummaryReload = { loadSummary(summaryId) },
        )
    }

    // Collections

    fun showAddToCollection() {
        collectionDelegate.showAddToCollection(
            scope = viewModelScope,
            currentState = { _state.value.collection },
        ) { sub -> _state.update { it.copy(collection = sub) } }
    }

    fun dismissAddToCollection() {
        collectionDelegate.dismissAddToCollection(
            currentState = { _state.value.collection },
        ) { sub -> _state.update { it.copy(collection = sub) } }
    }

    fun addToCollection(collectionId: String) {
        val summaryId = _state.value.summary?.id ?: return
        collectionDelegate.addToCollection(
            collectionId = collectionId,
            summaryId = summaryId,
            scope = viewModelScope,
            currentState = { _state.value.collection },
        ) { sub -> _state.update { it.copy(collection = sub) } }
    }

    // Export / Share

    fun exportSummary() {
        val summaryId = _state.value.summary?.id ?: return
        _state.update { it.copy(isExporting = true, exportError = null) }
        viewModelScope.launch {
            runCatchingDomain {
                val markdown = exportSummaryUseCase(summaryId).getOrThrow()
                shareManager.shareText(markdown, "Export Summary")
            }
                .onSuccess { _state.update { it.copy(isExporting = false) } }
                .onFailure { e ->
                    _state.update { it.copy(isExporting = false, exportError = e.toAppError().userMessage()) }
                }
        }
    }

    override fun onDestroy() {
        readingSessionDelegate.endSession(
            scope = viewModelScope,
            currentState = { _state.value.session },
        ) { sub -> _state.update { it.copy(session = sub) } }
        super.onDestroy()
    }
}
