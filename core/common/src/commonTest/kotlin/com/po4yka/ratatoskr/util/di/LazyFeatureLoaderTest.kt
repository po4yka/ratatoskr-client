package com.po4yka.ratatoskr.util.di

import com.po4yka.ratatoskr.util.di.LazyFeatureLoader.FeatureKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LazyFeatureLoaderTest {
    @Test
    fun `first ensureLoaded triggers the load operation`() {
        val loads = mutableListOf<FeatureKey>()
        val loader = LazyFeatureLoader(loadOperation = { loads += it })

        loader.ensureLoaded(FeatureKey.DIGEST)

        assertEquals(listOf(FeatureKey.DIGEST), loads)
        assertTrue(FeatureKey.DIGEST in loader.loadedSnapshot())
    }

    @Test
    fun `subsequent calls for the same feature do not re-trigger the load`() {
        // Regression guard: if a contributor "fixed" the idempotence away, every
        // navigation to a deferred feature would re-execute its Koin module binding —
        // visible only as duplicated factory instances and confused dependency state.
        val loads = mutableListOf<FeatureKey>()
        val loader = LazyFeatureLoader(loadOperation = { loads += it })

        loader.ensureLoaded(FeatureKey.DIGEST)
        loader.ensureLoaded(FeatureKey.DIGEST)
        loader.ensureLoaded(FeatureKey.DIGEST)

        assertEquals(listOf(FeatureKey.DIGEST), loads, "load operation must fire exactly once")
    }

    @Test
    fun `different features each load independently`() {
        val loads = mutableListOf<FeatureKey>()
        val loader = LazyFeatureLoader(loadOperation = { loads += it })

        loader.ensureLoaded(FeatureKey.DIGEST)
        loader.ensureLoaded(FeatureKey.COLLECTIONS)
        loader.ensureLoaded(FeatureKey.SETTINGS)
        loader.ensureLoaded(FeatureKey.COLLECTIONS) // already loaded — skip

        assertEquals(
            listOf(FeatureKey.DIGEST, FeatureKey.COLLECTIONS, FeatureKey.SETTINGS),
            loads,
            "each feature loads once, in the order it was first requested",
        )
        assertEquals(
            setOf(FeatureKey.DIGEST, FeatureKey.COLLECTIONS, FeatureKey.SETTINGS),
            loader.loadedSnapshot(),
        )
    }

    @Test
    fun `loadedSnapshot returns an immutable copy that won't reflect future loads`() {
        // Defends against a future contributor exposing the internal mutable set:
        // a snapshot must be a point-in-time read, not a live view.
        val loader = LazyFeatureLoader(loadOperation = { /* no-op */ })

        loader.ensureLoaded(FeatureKey.DIGEST)
        val snapshot = loader.loadedSnapshot()
        loader.ensureLoaded(FeatureKey.SETTINGS)

        assertEquals(setOf(FeatureKey.DIGEST), snapshot, "snapshot must not see later loads")
    }
}
