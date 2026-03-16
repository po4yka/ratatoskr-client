package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.ArchiveSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.ToggleFavoriteUseCase
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.util.error.toAppError
import com.po4yka.bitesizereader.util.error.userMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

@Suppress("TooGenericExceptionCaught")
class SummaryActionHandler(
    private val scope: CoroutineScope,
    private val stateAccessor: StateAccessor<SummaryListState>,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val archiveSummaryUseCase: ArchiveSummaryUseCase,
) {
    fun markAsRead(id: String) {
        scope.launch {
            try {
                markSummaryAsReadUseCase(id)
                logger.debug { "Marked summary $id as read" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to mark summary as read: $id" }
            }
        }
    }

    fun deleteSummary(id: String) {
        scope.launch {
            try {
                deleteSummaryUseCase(id)
                stateAccessor.update { it.copy(summaries = it.summaries.filter { s -> s.id != id }) }
                logger.info { "Deleted summary $id" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to delete summary: $id" }
                stateAccessor.update { it.copy(error = e.toAppError().userMessage()) }
            }
        }
    }

    fun toggleFavorite(id: String) {
        scope.launch {
            try {
                toggleFavoriteUseCase(id)
                stateAccessor.update { current ->
                    current.copy(
                        summaries =
                            current.summaries.map {
                                if (it.id == id) it.copy(isFavorited = !it.isFavorited) else it
                            },
                    )
                }
                logger.debug { "Toggled favorite for summary $id" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to toggle favorite: $id" }
                stateAccessor.update { it.copy(error = e.toAppError().userMessage()) }
            }
        }
    }

    fun archiveSummary(id: String) {
        scope.launch {
            try {
                archiveSummaryUseCase(id)
                stateAccessor.update { it.copy(summaries = it.summaries.filter { s -> s.id != id }) }
                logger.info { "Archived summary $id" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to archive summary: $id" }
                stateAccessor.update { it.copy(error = e.toAppError().userMessage()) }
            }
        }
    }
}
