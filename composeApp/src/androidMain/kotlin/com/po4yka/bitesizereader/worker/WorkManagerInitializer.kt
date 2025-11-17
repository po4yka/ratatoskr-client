package com.po4yka.bitesizereader.worker

import android.content.Context
import androidx.work.*
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

/**
 * Initializes and configures WorkManager for background sync
 */
object WorkManagerInitializer {
    /**
     * Schedule periodic sync work
     * Runs every 6 hours with a 2-hour flex window
     */
    fun schedulePeriodicSync(context: Context) {
        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

        val syncRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(
                SyncWorker.SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS,
                SyncWorker.SYNC_FLEX_INTERVAL_HOURS,
                TimeUnit.HOURS,
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS,
                )
                .addTag(SyncWorker.WORK_NAME)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Don't replace existing work
            syncRequest,
        )

        logger.info { "Scheduled periodic background sync (every ${SyncWorker.SYNC_INTERVAL_HOURS}h)" }
    }

    /**
     * Trigger immediate one-time sync
     * Useful for manual refresh or after user actions
     */
    fun triggerImmediateSync(
        context: Context,
        forceFullSync: Boolean = false,
    ) {
        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val inputData =
            Data.Builder()
                .putBoolean(SyncWorker.KEY_FORCE_FULL_SYNC, forceFullSync)
                .build()

        val syncRequest =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .addTag("immediate_sync")
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "immediate_sync",
            ExistingWorkPolicy.REPLACE, // Replace any pending immediate sync
            syncRequest,
        )

        logger.info { "Triggered immediate sync (forceFullSync=$forceFullSync)" }
    }

    /**
     * Cancel all scheduled sync work
     * Useful for logout or when user disables background sync
     */
    fun cancelSync(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(SyncWorker.WORK_NAME)
        logger.info { "Cancelled all background sync work" }
    }

    /**
     * Get sync work status
     */
    fun getSyncWorkInfo(context: Context) =
        WorkManager.getInstance(context)
            .getWorkInfosByTagLiveData(SyncWorker.WORK_NAME)
}
