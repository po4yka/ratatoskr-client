package com.po4yka.ratatoskr.util.observability

/**
 * Pure decision atom that collapses three sync-health signals into a
 * single tri-state badge for the Frost UI status strip and the
 * SyncDebugScreen header.
 *
 * Inputs:
 *  - `anyFailure`         — at least one categorized applier failure
 *                            was surfaced via SyncHealthSnapshot
 *  - `lastSyncAgeSeconds` — seconds since the last completed sync
 *  - `pendingOpDepth`     — number of offline writes waiting to drain
 *
 * Decision (strongest wins):
 *  1. `anyFailure=true`                 → [Status.Failing] (red)
 *  2. `lastSyncAgeSeconds > STALE_AFTER`→ [Status.Degraded] (yellow)
 *  3. `pendingOpDepth > 0`              → [Status.Degraded] (yellow)
 *  4. otherwise                         → [Status.Healthy] (green)
 *
 * Defensive: negative age / pending-depth (clock skew, SQL count
 * arithmetic noise) is clamped to `0` so a transient bad value
 * doesn't flip the badge to Degraded.
 *
 * Pure, side-effect-free, deterministic. Composes with
 * `feature/sync` health-snapshot data without depending on that
 * module's domain model — accepts three primitive signals so any
 * surface can consume.
 */
object SyncHealthBadge {
    enum class Status {
        Healthy,
        Degraded,
        Failing,
    }

    const val STALE_AFTER_SECONDS: Long = 5L * 60L

    fun classify(
        anyFailure: Boolean,
        lastSyncAgeSeconds: Long,
        pendingOpDepth: Int,
    ): Status {
        if (anyFailure) return Status.Failing
        val safeAge = if (lastSyncAgeSeconds < 0) 0 else lastSyncAgeSeconds
        val safeDepth = if (pendingOpDepth < 0) 0 else pendingOpDepth
        if (safeAge > STALE_AFTER_SECONDS) return Status.Degraded
        if (safeDepth > 0) return Status.Degraded
        return Status.Healthy
    }
}
