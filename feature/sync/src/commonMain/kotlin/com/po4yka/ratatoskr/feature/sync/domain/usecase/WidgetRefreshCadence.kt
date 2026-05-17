package com.po4yka.ratatoskr.feature.sync.domain.usecase

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Decision atom for the cross-platform widget refresh worker. Wraps the
 * battery / network / freshness rules into a typed allow-or-defer outcome so
 * the Android `WidgetRefreshWorker` (WorkManager) and the iOS
 * `BGAppRefreshTask` handler share one truth table.
 *
 * Why this is separate from the existing `PrefetchGate` and the
 * `RefreshRecentSummariesUseCase` battery guard:
 *
 *  - `PrefetchGate` decides large-payload background prefetch over a byte
 *    budget; widget refresh is small enough that a metered allowance is a
 *    legitimate user setting, not a per-cycle byte cap.
 *  - `RefreshRecentSummariesUseCase.invoke()` short-circuits on
 *    `BatteryStatus.isLow()`; this cadence atom layers network + freshness
 *    rules on top so the worker can decide whether to even call the use case.
 *
 * Freshness rule guards against the OS firing the worker more often than the
 * configured 30-minute cadence (WorkManager flex windows, iOS BGTaskScheduler
 * batching). The cadence threshold uses `>=` so a wake-up exactly at the
 * minimum interval is allowed; a strict `>` would create perpetual deferral
 * on a clock that ticks in whole minutes.
 *
 * Clock-skew tolerance: a `lastSuccessAt` in the future (after a timezone
 * resync) is treated as fresh, not as never-refreshed. The alternative —
 * computing `abs(now - lastSuccessAt) >= interval` — would let a 6-hour
 * forward skew force an immediate refresh.
 *
 * Precedence (top to bottom): BATTERY_LOW → OFFLINE → METERED_BLOCKED →
 * RECENTLY_REFRESHED → Allow.
 */
object WidgetRefreshCadence {
    const val MIN_INTERVAL_MINUTES: Long = 25

    enum class NetworkClass { UNMETERED, METERED, OFFLINE }

    enum class NetworkPolicy { UNMETERED_ONLY, ANY_NETWORK }

    enum class DeferReason {
        BATTERY_LOW,
        OFFLINE,
        METERED_BLOCKED,
        RECENTLY_REFRESHED,
    }

    sealed interface Decision {
        data object Allow : Decision

        data class Defer(val reason: DeferReason) : Decision
    }

    fun decide(
        isBatteryLow: Boolean,
        networkClass: NetworkClass,
        lastSuccessAt: Instant?,
        now: Instant,
        policy: NetworkPolicy,
    ): Decision {
        if (isBatteryLow) return Decision.Defer(DeferReason.BATTERY_LOW)
        if (networkClass == NetworkClass.OFFLINE) return Decision.Defer(DeferReason.OFFLINE)
        if (networkClass == NetworkClass.METERED && policy == NetworkPolicy.UNMETERED_ONLY) {
            return Decision.Defer(DeferReason.METERED_BLOCKED)
        }
        if (lastSuccessAt != null) {
            val elapsed = now - lastSuccessAt
            if (elapsed < MIN_INTERVAL_MINUTES.minutes) {
                return Decision.Defer(DeferReason.RECENTLY_REFRESHED)
            }
        }
        return Decision.Allow
    }
}
