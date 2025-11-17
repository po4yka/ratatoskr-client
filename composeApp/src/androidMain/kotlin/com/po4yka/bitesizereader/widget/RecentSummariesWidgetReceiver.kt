package com.po4yka.bitesizereader.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.*
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

/**
 * Receiver for Recent Summaries widget.
 *
 * Handles widget lifecycle events:
 * - Widget enabled: Schedule periodic updates
 * - Widget updated: Refresh content
 * - Widget disabled: Cancel periodic updates
 */
class RecentSummariesWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    override val glanceAppWidget: GlanceAppWidget = RecentSummariesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        logger.info { "Widget enabled, scheduling periodic updates" }
        scheduleWidgetUpdates(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        logger.debug { "Widget update requested for ${appWidgetIds.size} widgets" }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        logger.info { "Widget disabled, canceling periodic updates" }
        cancelWidgetUpdates(context)
    }

    companion object {
        private const val WIDGET_UPDATE_WORK_NAME = "widget_update_work"

        /**
         * Schedule periodic widget updates.
         *
         * Updates occur every 1 hour to keep widget content fresh while
         * minimizing battery impact.
         */
        fun scheduleWidgetUpdates(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val updateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                1, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WIDGET_UPDATE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest
            )

            logger.debug { "Scheduled periodic widget updates (every 1 hour)" }
        }

        /**
         * Cancel periodic widget updates.
         */
        fun cancelWidgetUpdates(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WIDGET_UPDATE_WORK_NAME)
            logger.debug { "Cancelled periodic widget updates" }
        }
    }
}

/**
 * WorkManager worker for updating widget in background.
 *
 * Syncs data and updates all widget instances.
 */
class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val syncDataUseCase: SyncDataUseCase by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        logger.debug { "Starting widget update worker" }

        try {
            // Sync data from server
            val syncResult = syncDataUseCase(forceFullSync = false)

            if (syncResult.isSuccess) {
                // Update all widget instances
                RecentSummariesWidget().updateAll(applicationContext)
                logger.info { "Widget updated successfully" }
                Result.success()
            } else {
                logger.warn { "Sync failed, retrying widget update" }
                Result.retry()
            }
        } catch (e: Exception) {
            logger.error(e) { "Widget update failed" }
            Result.retry()
        }
    }
}
