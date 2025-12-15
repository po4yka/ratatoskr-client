package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.util.error.AppError
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

private const val DEFAULT_PAGE_SIZE = 20

@Factory
class SummaryListViewModel(
    private val getSummariesUseCase: GetSummariesUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(SummaryListState())
    val state = _state.asStateFlow()

    init {
        syncAndLoad()
    }

    /**
     * Syncs data from server and then loads summaries from local database.
     * Called on init and when user triggers refresh.
     */
    @Suppress("TooGenericExceptionCaught")
    fun syncAndLoad() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                syncDataUseCase()
                logger.info { "Sync completed successfully" }
            } catch (_: AppError.SessionExpiredError) {
                logger.warn { "Session expired, triggering re-authentication" }
                logoutUseCase()
                // Continue to load local data despite auth error
            } catch (e: Exception) {
                logger.warn(e) { "Sync failed, loading from local cache" }
            }
            loadSummariesFromDatabase()
        }
    }

    /**
     * Loads summaries from local database without syncing.
     */
    fun loadSummaries() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            loadSummariesFromDatabase()
        }
    }

    private suspend fun loadSummariesFromDatabase() {
        getSummariesUseCase(_state.value.page, DEFAULT_PAGE_SIZE, listOfNotNull(_state.value.selectedTag))
            .catch { e ->
                logger.error(e) { "Failed to load summaries" }
                _state.value = _state.value.copy(isLoading = false, error = e.toAppError().userMessage())
            }
            .collect { summaries ->
                _state.value =
                    _state.value.copy(
                        summaries = summaries,
                        isLoading = false,
                        error = null,
                    )
            }
    }

    @Suppress("unused") // Public API for UI layer
    fun onTagSelected(tag: String?) {
        _state.value = _state.value.copy(selectedTag = tag, page = 1, summaries = emptyList())
        loadSummaries()
    }

    @Suppress("unused") // Public API for UI layer
    fun markAsRead(id: String) {
        viewModelScope.launch {
            markSummaryAsReadUseCase(id)
        }
    }
}
