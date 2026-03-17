package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.domain.repository.ReadingPreferencesRepository
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryContentUseCase
import com.po4yka.bitesizereader.domain.usecase.RefreshFullContentUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
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
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    init {
        readingPreferencesRepository.getPreferences()
            .onEach { prefs ->
                _state.value = _state.value.copy(readingPreferences = prefs)
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

    // Audio

    fun generateAndPlayAudio(sourceField: String = "summary_1000") {
        val summaryId = _state.value.summary?.id?.toLongOrNull() ?: return
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

    override fun onDestroy() {
        readingSessionDelegate.endSession(
            scope = viewModelScope,
            currentState = { _state.value.session },
        ) { sub -> _state.update { it.copy(session = sub) } }
        super.onDestroy()
    }
}
