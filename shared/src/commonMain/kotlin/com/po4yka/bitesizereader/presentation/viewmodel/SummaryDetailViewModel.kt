package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.model.AudioPlaybackState
import com.po4yka.bitesizereader.domain.model.AudioStatus
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import com.po4yka.bitesizereader.domain.repository.ReadingPreferencesRepository
import com.po4yka.bitesizereader.domain.usecase.AddToCollectionUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GenerateAudioUseCase
import com.po4yka.bitesizereader.domain.usecase.GetAudioUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryContentUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.RefreshFullContentUseCase
import com.po4yka.bitesizereader.domain.usecase.SaveReadPositionUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.bitesizereader.util.audio.AudioPlayer
import com.po4yka.bitesizereader.presentation.state.SummaryDetailState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryDetailState())
    val state = _state.asStateFlow()

    init {
        readingPreferencesRepository.getPreferences()
            .onEach { prefs ->
                _state.value = _state.value.copy(readingPreferences = prefs)
            }
            .launchIn(viewModelScope)
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
                    fetchFullContent(id)
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

    fun saveReadPosition(
        position: Int,
        offset: Int,
    ) {
        val summaryId = _state.value.summary?.id ?: return
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
