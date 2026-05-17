package com.po4yka.ratatoskr.util.di

/**
 * Cold start resolves the entire DI graph today — including features the user hasn't
 * navigated to yet. This loader lets Decompose component factories for non-critical
 * features (digest, collections, settings) defer their Koin module load until first
 * navigation, leaving the cold-start critical path with only auth, summary, sync, core.
 *
 * The semantics are deliberately tiny:
 *  - [ensureLoaded] is idempotent. Calling it twice for the same key triggers the load
 *    operation exactly once. Subsequent navigations to the same feature are a no-op.
 *  - The load operation is supplied by the caller — typically
 *    `{ key -> koin.loadModules(modulesFor(key)) }`. Keeping the operation injectable
 *    keeps this class Koin-agnostic and unit-testable without a real Koin runtime.
 *
 * No internal locking. Decompose navigation events arrive on the main thread, and
 * even if a double-load slipped through, Koin's `loadModules` is itself idempotent
 * (re-binding the same definition is a no-op) so the worst case is one redundant
 * call rather than a graph corruption.
 *
 * Wiring is the caller's responsibility: instantiate one `LazyFeatureLoader` in
 * `KoinInitializer`, register it as a `@Single`, and have each non-critical feature's
 * Decompose component factory invoke `loader.ensureLoaded(...)` before resolving its
 * first dependency. The eager modules — auth, summary, sync, core/* — stay loaded
 * unconditionally at startup as today.
 */
class LazyFeatureLoader(
    private val loadOperation: (FeatureKey) -> Unit,
) {
    private val loaded = mutableSetOf<FeatureKey>()

    fun ensureLoaded(feature: FeatureKey) {
        if (loaded.add(feature)) {
            loadOperation(feature)
        }
    }

    /** Snapshot of which features have been loaded so far. For debugging / Sync Health screen. */
    fun loadedSnapshot(): Set<FeatureKey> = loaded.toSet()

    /** Non-startup features that opt into lazy DI loading. Mirror in `KoinInitializer`. */
    enum class FeatureKey { DIGEST, COLLECTIONS, SETTINGS }
}
