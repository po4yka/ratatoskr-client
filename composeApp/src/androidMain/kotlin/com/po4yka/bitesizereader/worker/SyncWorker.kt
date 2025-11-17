package com.po4yka.bitesizereader.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = KotlinLogging.logger {}

/**
 * Background worker for syncing data with server
 * Uses WorkManager to schedule periodic sync jobs
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {
    private val syncDataUseCase: SyncDataUseCase by inject()

    override suspend fun doWork(): Result {
        logger.info { "Starting background sync" }

        return try {
            // Determine if this is a forced full sync
            val forceFullSync = inputData.getBoolean(KEY_FORCE_FULL_SYNC, false)

            // Execute sync
            val result = syncDataUseCase(forceFullSync = forceFullSync)

            result.fold(
                onSuccess = {
                    logger.info { "Background sync completed successfully" }
                    Result.success()
                },
                onFailure = { error ->
                    logger.error(error) { "Background sync failed" }
                    // Retry on failure (WorkManager will handle backoff)
                    Result.retry()
                },
            )
        } catch (e: Exception) {
            logger.error(e) { "Exception during background sync" }
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_worker"
        const val KEY_FORCE_FULL_SYNC = "force_full_sync"

        // Sync frequency constraints
        const val SYNC_INTERVAL_HOURS = 6L
        const val SYNC_FLEX_INTERVAL_HOURS = 2L
    }
}
