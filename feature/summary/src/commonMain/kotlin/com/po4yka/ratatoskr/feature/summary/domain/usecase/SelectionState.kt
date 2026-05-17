package com.po4yka.ratatoskr.feature.summary.domain.usecase

/**
 * Immutable state machine behind the long-press → bulk-toolbar UX on SummaryListScreen.
 *
 * The contract is:
 *  - [start] is the long-press entrypoint. It replaces any prior selection with just the
 *    seeded id — long-press is the *enter selection mode* gesture, not an accumulator.
 *  - [toggle] is the plain-tap-during-selection gesture. It adds or removes the id, and
 *    if removing the last item brings the set empty it also exits selection mode (so
 *    the toolbar disappears, matching back-button parity).
 *  - [clear] is the explicit "Back / Esc / navigation away" handler.
 *
 * Modeled as a value class on top of `Set<T>` so it composes cleanly with Compose's
 * structural-equality recomposition skipping. Generic over the id type so the same
 * algorithm covers summary ids, highlight ids, anything else that needs bulk selection.
 */
data class SelectionState<T>(val selectedIds: Set<T>) {
    val count: Int get() = selectedIds.size
    val isActive: Boolean get() = selectedIds.isNotEmpty()

    fun contains(id: T): Boolean = id in selectedIds

    fun start(id: T): SelectionState<T> = SelectionState(setOf(id))

    fun toggle(id: T): SelectionState<T> =
        if (id in selectedIds) SelectionState(selectedIds - id) else SelectionState(selectedIds + id)

    fun clear(): SelectionState<T> = empty()

    companion object {
        fun <T> empty(): SelectionState<T> = SelectionState(emptySet())
    }
}
