package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.SummariesApi
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class ToggleFavoriteUseCase(
    private val repository: SummaryRepository,
    private val api: SummariesApi,
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(id: String) {
        // Optimistic local update first
        repository.toggleFavorite(id)

        // Then sync with server
        val remoteId = id.toLongOrNull()
        if (remoteId != null) {
            try {
                api.toggleFavorite(remoteId)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to toggle favorite on server for $id, reverting" }
                // Revert on failure
                repository.toggleFavorite(id)
                throw e
            }
        }
    }
}
