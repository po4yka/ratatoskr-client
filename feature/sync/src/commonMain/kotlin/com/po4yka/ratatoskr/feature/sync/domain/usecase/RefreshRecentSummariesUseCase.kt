package com.po4yka.ratatoskr.feature.sync.domain.usecase

import com.po4yka.ratatoskr.feature.sync.domain.repository.SyncRepository
import com.po4yka.ratatoskr.util.battery.BatteryStatus
import com.po4yka.ratatoskr.util.error.runCatchingDomain
import org.koin.core.annotation.Factory

/**
 * Short-cadence top-up sync intended for the recent-summaries widget on Android
 * (`WidgetRefreshWorker`, 30-min flex `PeriodicWorkRequest`) and iOS
 * (`BGAppRefreshTaskRequest`, 30-min minimum interval).
 *
 * Stays separate from the full 6-hour `SyncWorker` / `BGProcessingTaskRequest` flow
 * because the widget cares about the freshest few entries, not a complete reconciliation
 * — and short-cadence work needs to be cheap enough that the OS keeps scheduling it.
 *
 * Battery gating is enforced here so the platform schedulers don't have to know about
 * it: the use case probes [BatteryStatus.isLow] before doing any work and returns
 * [RefreshOutcome.Skipped] if so. The OS-level "don't run on low battery" constraints
 * provided by WorkManager / BGTaskScheduler are coarser and platform-specific; this
 * gate is the cross-platform fallback that means a misconfigured worker spec doesn't
 * drain the battery.
 *
 * Failures (network, server 5xx, etc.) are caught via [runCatchingDomain] and returned
 * as [RefreshOutcome.Failed] so the caller can decide whether to surface a toast or
 * silently swallow — background workers do the latter.
 */
@Factory
class RefreshRecentSummariesUseCase(
    private val syncRepository: SyncRepository,
    private val batteryStatus: BatteryStatus,
) {
    /**
     * Runs a delta sync via [SyncRepository.sync] (`forceFull = false`) when the battery
     * is healthy enough. Returns the outcome — callers should not rely on this for
     * error-message text, that's the responsibility of foreground sync screens.
     */
    suspend operator fun invoke(): RefreshOutcome {
        if (batteryStatus.isLow()) {
            return RefreshOutcome.Skipped(SkipReason.BATTERY_LOW)
        }

        return runCatchingDomain { syncRepository.sync(forceFull = false) }
            .fold(
                onSuccess = { RefreshOutcome.Refreshed },
                onFailure = { e -> RefreshOutcome.Failed(e) },
            )
    }

    sealed interface RefreshOutcome {
        /** Delta sync completed; the widget can read updated rows from the DB on next paint. */
        data object Refreshed : RefreshOutcome

        /** The use case decided not to run. [reason] is for telemetry, not user-facing. */
        data class Skipped(val reason: SkipReason) : RefreshOutcome

        /**
         * Delta sync threw. [cause] preserves the original exception so the caller can
         * log it; the user-facing message is not the use case's concern.
         */
        data class Failed(val cause: Throwable) : RefreshOutcome
    }

    enum class SkipReason { BATTERY_LOW }
}
