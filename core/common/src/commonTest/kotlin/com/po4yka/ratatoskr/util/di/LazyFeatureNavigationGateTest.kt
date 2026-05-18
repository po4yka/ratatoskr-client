package com.po4yka.ratatoskr.util.di

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LazyFeatureNavigationGateTest {
    @Test
    fun `Auth — EagerPassThrough — startup-critical, never lazy`() {
        // Eager-set features bypass the gate so the bootstrap path
        // never has to wait on a load operation that is already done.
        assertEquals(
            NavigationGateDecision.EagerPassThrough,
            LazyFeatureNavigationGate.decide(target = NavigationTarget.Auth, loaded = emptySet()),
        )
    }

    @Test
    fun `Summary — EagerPassThrough`() {
        assertEquals(
            NavigationGateDecision.EagerPassThrough,
            LazyFeatureNavigationGate.decide(target = NavigationTarget.Summary, loaded = emptySet()),
        )
    }

    @Test
    fun `Sync — EagerPassThrough`() {
        assertEquals(
            NavigationGateDecision.EagerPassThrough,
            LazyFeatureNavigationGate.decide(target = NavigationTarget.Sync, loaded = emptySet()),
        )
    }

    @Test
    fun `Digest with empty loaded set — LoadThenNavigate(DIGEST)`() {
        assertEquals(
            NavigationGateDecision.LoadThenNavigate(key = LazyFeatureLoader.FeatureKey.DIGEST),
            LazyFeatureNavigationGate.decide(
                target = NavigationTarget.Digest,
                loaded = emptySet(),
            ),
        )
    }

    @Test
    fun `Digest already loaded — LazyAlreadyLoaded`() {
        // Idempotent — second navigation to a lazy feature is a no-op
        // from the gate's perspective.
        assertEquals(
            NavigationGateDecision.LazyAlreadyLoaded,
            LazyFeatureNavigationGate.decide(
                target = NavigationTarget.Digest,
                loaded = setOf(LazyFeatureLoader.FeatureKey.DIGEST),
            ),
        )
    }

    @Test
    fun `Collections with unrelated key loaded — still LoadThenNavigate`() {
        // Pin per-feature independence: having DIGEST loaded does not
        // satisfy a Collections gate.
        assertEquals(
            NavigationGateDecision.LoadThenNavigate(key = LazyFeatureLoader.FeatureKey.COLLECTIONS),
            LazyFeatureNavigationGate.decide(
                target = NavigationTarget.Collections,
                loaded = setOf(LazyFeatureLoader.FeatureKey.DIGEST),
            ),
        )
    }

    @Test
    fun `Settings with all three loaded — LazyAlreadyLoaded`() {
        assertEquals(
            NavigationGateDecision.LazyAlreadyLoaded,
            LazyFeatureNavigationGate.decide(
                target = NavigationTarget.Settings,
                loaded =
                    setOf(
                        LazyFeatureLoader.FeatureKey.DIGEST,
                        LazyFeatureLoader.FeatureKey.COLLECTIONS,
                        LazyFeatureLoader.FeatureKey.SETTINGS,
                    ),
            ),
        )
    }

    @Test
    fun `decide is exhaustive over NavigationTarget`() {
        // Pin so a new NavigationTarget entry forces a compile error in
        // the gate's `when` rather than silently returning a default.
        // The smoke test here just iterates and confirms every target
        // produces a decision.
        NavigationTarget.entries.forEach { target ->
            val decision =
                LazyFeatureNavigationGate.decide(target = target, loaded = emptySet())
            assertTrue(decision is NavigationGateDecision)
        }
    }

    @Test
    fun `decide is deterministic`() {
        val loaded = setOf(LazyFeatureLoader.FeatureKey.DIGEST)
        val a = LazyFeatureNavigationGate.decide(target = NavigationTarget.Digest, loaded = loaded)
        val b = LazyFeatureNavigationGate.decide(target = NavigationTarget.Digest, loaded = loaded)
        assertEquals(a, b)
    }
}
