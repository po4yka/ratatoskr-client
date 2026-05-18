package com.po4yka.ratatoskr.util.di

/**
 * Every routed feature in the Decompose root stack. Some are loaded
 * eagerly at startup ([Auth], [Summary], [Sync]) and some defer their
 * Koin module load until first navigation ([Digest], [Collections],
 * [Settings]).
 */
enum class NavigationTarget {
    Auth,
    Summary,
    Sync,
    Digest,
    Collections,
    Settings,
}

/**
 * What the routing shell should do when a navigation request lands on
 * a given [NavigationTarget].
 */
sealed interface NavigationGateDecision {
    /** Target is in the eager set; navigate now, no gate needed. */
    data object EagerPassThrough : NavigationGateDecision

    /** Target is lazy but already loaded; navigate now. */
    data object LazyAlreadyLoaded : NavigationGateDecision

    /** Target is lazy and not loaded yet; load this key first, then navigate. */
    data class LoadThenNavigate(val key: LazyFeatureLoader.FeatureKey) : NavigationGateDecision
}

/**
 * Pure (target, loaded) → decision atom. Companion to
 * [LazyFeatureLoader] — that class owns the actual load operation;
 * this atom owns the rule for *when* to trigger one.
 *
 * Decompose component factories for digest, collections, and settings
 * fold their navigation requests through this gate; the routing shell
 * uses the decision to either pass through to the factory or hand off
 * to [LazyFeatureLoader.ensureLoaded] before the factory resolves its
 * first dependency.
 *
 * Pure, side-effect-free, deterministic.
 */
object LazyFeatureNavigationGate {
    fun decide(
        target: NavigationTarget,
        loaded: Set<LazyFeatureLoader.FeatureKey>,
    ): NavigationGateDecision {
        val lazyKey = lazyKeyOf(target) ?: return NavigationGateDecision.EagerPassThrough
        return if (lazyKey in loaded) {
            NavigationGateDecision.LazyAlreadyLoaded
        } else {
            NavigationGateDecision.LoadThenNavigate(key = lazyKey)
        }
    }

    private fun lazyKeyOf(target: NavigationTarget): LazyFeatureLoader.FeatureKey? =
        when (target) {
            NavigationTarget.Digest -> LazyFeatureLoader.FeatureKey.DIGEST
            NavigationTarget.Collections -> LazyFeatureLoader.FeatureKey.COLLECTIONS
            NavigationTarget.Settings -> LazyFeatureLoader.FeatureKey.SETTINGS
            NavigationTarget.Auth,
            NavigationTarget.Summary,
            NavigationTarget.Sync,
            -> null
        }
}
