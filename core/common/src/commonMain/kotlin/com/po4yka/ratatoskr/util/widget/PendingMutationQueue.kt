package com.po4yka.ratatoskr.util.widget

/**
 * Action kind for a widget-side mutation queued for host-app drain.
 * Mirrors [WidgetRowAction] but lives next to the queue because the
 * App Group payload on iOS is just a flat list — the sealed-with-id
 * shape is overkill there.
 */
enum class WidgetMutationAction {
    MarkRead,
    Archive,
}

/**
 * A single mutation produced by a tap on an iOS widget intent (or its
 * Android Glance equivalent), queued in App Group / Glance state until
 * the host app drains it on next foreground.
 *
 * `createdAtMillis` is the wall-clock when the intent fired, used both
 * for drain ordering and dedup precedence.
 */
data class PendingWidgetMutation(
    val summaryId: String,
    val action: WidgetMutationAction,
    val createdAtMillis: Long,
)

/**
 * Pure queue manipulator for the cross-process pending-mutation list
 * the iOS widget writes to its App Group store (and the Android Glance
 * row callback writes to Glance state).
 *
 * Properties pinned here:
 *  1. Dedup keyed on (summaryId, action). Tapping Mark-Read twice in a
 *     row enqueues one drain event, not two. The newer timestamp wins;
 *     stale replays after a clock skew do not overwrite a fresh action.
 *  2. Different ids and different actions on the same id coexist as
 *     separate entries. Archive on the same summary is a distinct
 *     intent — the drain decides apply-order.
 *  3. Output is sorted ascending by `createdAtMillis`, matching the
 *     user's tap order during the offline window.
 *  4. Bounded at [MAX_QUEUE_SIZE]. App Group storage is space-bounded
 *     by iOS; an unbounded queue across long-suspended sessions could
 *     refuse new mutations. Oldest is evicted first.
 *  5. Blank `summaryId` rejects the enqueue. The drain handler can
 *     rely on `summaryId.isNotBlank()` without re-checking.
 *
 * Pure, side-effect-free, deterministic.
 */
object PendingMutationQueue {
    const val MAX_QUEUE_SIZE: Int = 64

    fun enqueue(
        queue: List<PendingWidgetMutation>,
        mutation: PendingWidgetMutation,
    ): List<PendingWidgetMutation> {
        if (mutation.summaryId.isBlank()) return queue
        val existingIndex =
            queue.indexOfFirst {
                it.summaryId == mutation.summaryId && it.action == mutation.action
            }
        val merged: List<PendingWidgetMutation> =
            when {
                existingIndex < 0 -> queue + mutation
                mutation.createdAtMillis > queue[existingIndex].createdAtMillis ->
                    queue.toMutableList().also { it[existingIndex] = mutation }
                else -> queue
            }
        val sorted = merged.sortedBy { it.createdAtMillis }
        return if (sorted.size > MAX_QUEUE_SIZE) sorted.takeLast(MAX_QUEUE_SIZE) else sorted
    }
}
