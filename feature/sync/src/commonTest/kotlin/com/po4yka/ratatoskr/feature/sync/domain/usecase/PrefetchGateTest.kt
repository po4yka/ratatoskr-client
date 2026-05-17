package com.po4yka.ratatoskr.feature.sync.domain.usecase

import com.po4yka.ratatoskr.feature.sync.domain.usecase.PrefetchGate.Decision
import com.po4yka.ratatoskr.feature.sync.domain.usecase.PrefetchGate.DenyReason
import com.po4yka.ratatoskr.feature.sync.domain.usecase.PrefetchGate.NetworkClass
import com.po4yka.ratatoskr.feature.sync.domain.usecase.PrefetchGate.PrefetchPolicy
import kotlin.test.Test
import kotlin.test.assertEquals

class PrefetchGateTest {
    @Test
    fun `OFF policy denies regardless of network`() {
        for (network in NetworkClass.entries) {
            val decision =
                PrefetchGate.evaluate(
                    policy = PrefetchPolicy.OFF,
                    network = network,
                    bytesDownloadedThisCycle = 0,
                )
            assertEquals(
                Decision.Deny(DenyReason.POLICY_OFF),
                decision,
                "policy OFF must veto every network class — got allow for $network",
            )
        }
    }

    @Test
    fun `OFFLINE network denies regardless of policy`() {
        for (policy in listOf(PrefetchPolicy.WIFI_ONLY, PrefetchPolicy.ALWAYS)) {
            val decision =
                PrefetchGate.evaluate(
                    policy = policy,
                    network = NetworkClass.OFFLINE,
                    bytesDownloadedThisCycle = 0,
                )
            assertEquals(Decision.Deny(DenyReason.OFFLINE), decision)
        }
    }

    @Test
    fun `WIFI_ONLY policy on metered network denies — this is the user-data-protection core`() {
        // Regression guard: the entire DoD of the feature is "with WIFI_ONLY enabled,
        // mobile-data traffic shows zero prefetch bytes". If this test ever passes Allow
        // for METERED, the feature has silently regressed for every cellular user.
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.WIFI_ONLY,
                network = NetworkClass.METERED,
                bytesDownloadedThisCycle = 0,
            )

        assertEquals(Decision.Deny(DenyReason.METERED_BLOCKED_BY_WIFI_ONLY), decision)
    }

    @Test
    fun `WIFI_ONLY policy on unmetered network allows`() {
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.WIFI_ONLY,
                network = NetworkClass.UNMETERED,
                bytesDownloadedThisCycle = 0,
            )
        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `ALWAYS policy on metered allows — opt-in tax goes to the user`() {
        // The user has explicitly chosen ALWAYS knowing it will use cellular data.
        // The gate must respect that choice rather than second-guessing.
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.ALWAYS,
                network = NetworkClass.METERED,
                bytesDownloadedThisCycle = 0,
            )
        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `cap is reached when bytes downloaded equals the limit — exclusive on the boundary`() {
        // Boundary: at exactly the cap the gate denies further bytes. The next request
        // *would* push us past the cap; deny before that, not after.
        val cap = PrefetchGate.DEFAULT_MAX_BYTES_PER_CYCLE
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.ALWAYS,
                network = NetworkClass.UNMETERED,
                bytesDownloadedThisCycle = cap,
            )

        assertEquals(Decision.Deny(DenyReason.CAP_REACHED), decision)
    }

    @Test
    fun `cap is not reached when bytes downloaded is one byte below the limit`() {
        val cap = PrefetchGate.DEFAULT_MAX_BYTES_PER_CYCLE
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.ALWAYS,
                network = NetworkClass.UNMETERED,
                bytesDownloadedThisCycle = cap - 1,
            )

        assertEquals(Decision.Allow, decision)
    }

    @Test
    fun `caller-supplied cap overrides the default — for telemetry-tuned cycles`() {
        // Future use case: a watch-companion or low-storage device tightens the cap.
        // The gate accepts a maxBytes argument so the algorithm doesn't have to grow
        // a global config dependency.
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.ALWAYS,
                network = NetworkClass.UNMETERED,
                bytesDownloadedThisCycle = 1_000_000,
                maxBytesPerCycle = 1_000_000,
            )
        assertEquals(Decision.Deny(DenyReason.CAP_REACHED), decision)
    }

    @Test
    fun `precedence is OFF then OFFLINE then METERED then CAP — fail fast on the cheapest signal`() {
        // Regression guard: if OFF and CAP could both deny, the gate should report
        // POLICY_OFF (cheaper to act on, more informative to telemetry). The order
        // is deliberate: a refactor that flips it would change telemetry semantics.
        val decision =
            PrefetchGate.evaluate(
                policy = PrefetchPolicy.OFF,
                network = NetworkClass.OFFLINE,
                bytesDownloadedThisCycle = PrefetchGate.DEFAULT_MAX_BYTES_PER_CYCLE,
            )
        assertEquals(Decision.Deny(DenyReason.POLICY_OFF), decision)
    }
}
