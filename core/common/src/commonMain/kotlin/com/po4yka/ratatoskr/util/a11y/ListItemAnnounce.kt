package com.po4yka.ratatoskr.util.a11y

/**
 * Pure builder for the screen-reader announce string attached to a list
 * item via Compose `semantics { contentDescription = ListItemAnnounce.announce(...) }`.
 *
 * Centralizes the canonical format so every list surface produces the
 * same `"Item N of M, <text>"` phrase, matching the announce grammar
 * TalkBack and VoiceOver use natively for collection-view rows.
 *
 * Defensive contract:
 *  - `indexOneBased` is clamped to `[1, totalCount]`. Zero / negative
 *    (caller using 0-based mistake) collapses to `1`. Beyond-the-end
 *    (a racing scroll-position calculation) collapses to `totalCount`.
 *  - `totalCount <= 0` returns `""`. A list with no items should not
 *    announce a position; the caller can skip setting the semantics.
 *  - `itemText` is trimmed; blank text drops the trailing
 *    `", <text>"` so the announce stays `"Item N of M"` without
 *    awkward comma-space noise.
 *
 * Pure, side-effect-free, deterministic.
 */
object ListItemAnnounce {
    fun announce(
        indexOneBased: Int,
        totalCount: Int,
        itemText: String,
    ): String {
        if (totalCount <= 0) return ""
        val clampedIndex = indexOneBased.coerceIn(1, totalCount)
        val cleanedText = itemText.trim()
        val prefix = "Item $clampedIndex of $totalCount"
        return if (cleanedText.isEmpty()) prefix else "$prefix, $cleanedText"
    }
}
