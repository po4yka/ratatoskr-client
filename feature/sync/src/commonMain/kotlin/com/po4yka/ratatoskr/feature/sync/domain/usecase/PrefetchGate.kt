package com.po4yka.ratatoskr.feature.sync.domain.usecase

/**
 * Pure decision atom behind the Wi-Fi-aware full-content prefetcher. Given the user's
 * policy choice, the current network class, and the byte budget already spent in the
 * current cycle, returns [Decision.Allow] or [Decision.Deny] with a structured reason
 * so callers (and telemetry) can distinguish "user turned it off" from "we hit the
 * 50MB cap" from "we're on cellular and the user said Wi-Fi only".
 *
 * The gate has no I/O and no platform knowledge — it consumes a [NetworkClass] sampled
 * by the caller from `ConnectivityManager.getNetworkCapabilities` (Android) or
 * `NWPathMonitor` (iOS). This keeps the algorithm KMP-clean and unit-testable.
 *
 * Precedence on the deny side, fail-fast in this order: POLICY_OFF, OFFLINE,
 * METERED_BLOCKED_BY_WIFI_ONLY, CAP_REACHED. The ordering is deliberate — a
 * disabled-by-user prefetch should report POLICY_OFF even when the cap is also
 * exceeded, because that's the more actionable signal for telemetry.
 */
object PrefetchGate {
    const val DEFAULT_MAX_BYTES_PER_CYCLE: Long = 50L * 1024 * 1024

    enum class PrefetchPolicy { OFF, WIFI_ONLY, ALWAYS }

    enum class NetworkClass { UNMETERED, METERED, OFFLINE }

    enum class DenyReason { POLICY_OFF, OFFLINE, METERED_BLOCKED_BY_WIFI_ONLY, CAP_REACHED }

    sealed interface Decision {
        data object Allow : Decision

        data class Deny(val reason: DenyReason) : Decision
    }

    fun evaluate(
        policy: PrefetchPolicy,
        network: NetworkClass,
        bytesDownloadedThisCycle: Long,
        maxBytesPerCycle: Long = DEFAULT_MAX_BYTES_PER_CYCLE,
    ): Decision {
        if (policy == PrefetchPolicy.OFF) return Decision.Deny(DenyReason.POLICY_OFF)
        if (network == NetworkClass.OFFLINE) return Decision.Deny(DenyReason.OFFLINE)
        if (policy == PrefetchPolicy.WIFI_ONLY && network == NetworkClass.METERED) {
            return Decision.Deny(DenyReason.METERED_BLOCKED_BY_WIFI_ONLY)
        }
        if (bytesDownloadedThisCycle >= maxBytesPerCycle) return Decision.Deny(DenyReason.CAP_REACHED)
        return Decision.Allow
    }
}
