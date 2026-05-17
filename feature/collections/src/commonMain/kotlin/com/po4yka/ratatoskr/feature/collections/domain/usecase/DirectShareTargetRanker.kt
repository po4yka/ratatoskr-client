package com.po4yka.ratatoskr.feature.collections.domain.usecase

import kotlin.time.Instant

/**
 * Ranks the user's recently-used collections for the Android Direct-Share / dynamic-
 * shortcut surface. Given a flat list of usage events (one per save-to-collection),
 * produces the top [MAX_TARGETS] distinct collection ids ordered by most-recent use.
 *
 * The cap is the Android share-target ceiling ("Cap at 4 shortcuts to respect Android
 * limits" per spec). Re-using an older collection moves it back to the front — true
 * LRU semantics, not pure frequency — because the seeder is invoked after each
 * successful collection use to refresh the dynamic shortcuts, and the latest use is
 * the most relevant signal for the next share-sheet open.
 *
 * Pure function with no Android types; the platform seeder code (ShortcutManagerCompat
 * pushDynamicShortcut, MainActivity intent extras) consumes this ordered list of ids
 * directly.
 */
object DirectShareTargetRanker {
    const val MAX_TARGETS: Int = 4

    data class UsageEvent(val collectionId: String, val usedAt: Instant)

    fun rank(
        events: List<UsageEvent>,
        maxTargets: Int = MAX_TARGETS,
    ): List<String> =
        events.groupBy { it.collectionId }
            .mapValues { (_, group) -> group.maxOf { it.usedAt } }
            .entries
            .sortedByDescending { it.value }
            .take(maxTargets)
            .map { it.key }
}
