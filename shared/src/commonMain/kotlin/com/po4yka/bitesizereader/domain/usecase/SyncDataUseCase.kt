package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class SyncDataUseCase(
    private val repository: SyncRepository,
    private val prefetchContentUseCase: PrefetchContentUseCase,
) {
    /**
     * Observable progress of current sync operation (null if no sync in progress).
     */
    val syncProgress: StateFlow<SyncProgress?> = repository.syncProgress

    /**
     * Sync data with the backend, then prefetch content for recent unread articles.
     *
     * @param forceFull If true, performs a full sync (downloads all data).
     *                  If false, performs a delta sync (only changes since last sync).
     */
    suspend operator fun invoke(forceFull: Boolean = false) {
        repository.sync(forceFull = forceFull)
        // Prefetch content for recent unread articles after successful sync
        try {
            prefetchContentUseCase()
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            // Prefetch is best-effort — don't fail the sync
            logger.warn(e) { "Content prefetch after sync failed" }
        }
    }

    /**
     * Cancel the current sync operation if one is in progress.
     */
    fun cancelSync() {
        repository.cancelSync()
    }
}
