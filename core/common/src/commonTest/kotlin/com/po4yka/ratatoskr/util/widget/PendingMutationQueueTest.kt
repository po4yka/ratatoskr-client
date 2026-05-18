package com.po4yka.ratatoskr.util.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PendingMutationQueueTest {
    @Test
    fun `enqueue into empty queue — single entry`() {
        val out =
            PendingMutationQueue.enqueue(
                queue = emptyList(),
                mutation =
                    PendingWidgetMutation(
                        summaryId = "s1",
                        action = WidgetMutationAction.MarkRead,
                        createdAtMillis = 1_000,
                    ),
            )
        assertEquals(1, out.size)
        assertEquals("s1", out.first().summaryId)
    }

    @Test
    fun `dedup same summary and action — keep newest timestamp`() {
        // Tapping Mark-Read twice in a row from the widget must not enqueue
        // two drain events. The newest timestamp wins so the host applies
        // the latest user intent on next foreground.
        val older =
            PendingWidgetMutation(
                summaryId = "s1",
                action = WidgetMutationAction.MarkRead,
                createdAtMillis = 1_000,
            )
        val newer =
            PendingWidgetMutation(
                summaryId = "s1",
                action = WidgetMutationAction.MarkRead,
                createdAtMillis = 2_000,
            )
        val out = PendingMutationQueue.enqueue(queue = listOf(older), mutation = newer)
        assertEquals(1, out.size)
        assertEquals(2_000L, out.first().createdAtMillis)
    }

    @Test
    fun `dedup ignores stale enqueue — keep existing`() {
        // Out-of-order delivery (replay from disk after a clock skew) must
        // not overwrite a fresh user action with a stale one.
        val fresh =
            PendingWidgetMutation(
                summaryId = "s1",
                action = WidgetMutationAction.MarkRead,
                createdAtMillis = 2_000,
            )
        val stale =
            PendingWidgetMutation(
                summaryId = "s1",
                action = WidgetMutationAction.MarkRead,
                createdAtMillis = 1_000,
            )
        val out = PendingMutationQueue.enqueue(queue = listOf(fresh), mutation = stale)
        assertEquals(1, out.size)
        assertEquals(2_000L, out.first().createdAtMillis)
    }

    @Test
    fun `different summary ids coexist`() {
        val a =
            PendingWidgetMutation("s1", WidgetMutationAction.MarkRead, createdAtMillis = 1_000)
        val b =
            PendingWidgetMutation("s2", WidgetMutationAction.MarkRead, createdAtMillis = 2_000)
        val out = PendingMutationQueue.enqueue(queue = listOf(a), mutation = b)
        assertEquals(2, out.size)
    }

    @Test
    fun `different actions on same summary coexist`() {
        // Mark-Read + Archive on the same summary are distinct intents.
        // The host drains both — Archive may take precedence at apply
        // time, but that's the drain's responsibility, not the queue's.
        val read =
            PendingWidgetMutation("s1", WidgetMutationAction.MarkRead, createdAtMillis = 1_000)
        val archive =
            PendingWidgetMutation("s1", WidgetMutationAction.Archive, createdAtMillis = 2_000)
        val out = PendingMutationQueue.enqueue(queue = listOf(read), mutation = archive)
        assertEquals(2, out.size)
    }

    @Test
    fun `output is sorted by createdAtMillis ascending`() {
        // Pin the drain order so the host applies user intents in the
        // order the user pressed.
        val q =
            listOf(
                PendingWidgetMutation("s2", WidgetMutationAction.MarkRead, 3_000),
                PendingWidgetMutation("s1", WidgetMutationAction.MarkRead, 1_000),
            )
        val out =
            PendingMutationQueue.enqueue(
                queue = q,
                mutation = PendingWidgetMutation("s3", WidgetMutationAction.MarkRead, 2_000),
            )
        val ordered = out.map { it.createdAtMillis }
        assertEquals(listOf(1_000L, 2_000L, 3_000L), ordered)
    }

    @Test
    fun `overflow — oldest is evicted at MAX_QUEUE_SIZE`() {
        // App Group storage is bounded by iOS — the queue cannot grow
        // unbounded across host-suspended sessions. Evict the oldest to
        // make room for new intents.
        val full =
            (1..PendingMutationQueue.MAX_QUEUE_SIZE).map { i ->
                PendingWidgetMutation(
                    summaryId = "s$i",
                    action = WidgetMutationAction.MarkRead,
                    createdAtMillis = i.toLong(),
                )
            }
        val out =
            PendingMutationQueue.enqueue(
                queue = full,
                mutation =
                    PendingWidgetMutation(
                        summaryId = "new",
                        action = WidgetMutationAction.MarkRead,
                        createdAtMillis = 9_999,
                    ),
            )
        assertEquals(PendingMutationQueue.MAX_QUEUE_SIZE, out.size)
        assertTrue(out.none { it.summaryId == "s1" }, "oldest must be evicted")
        assertTrue(out.any { it.summaryId == "new" }, "newest must be present")
    }

    @Test
    fun `enqueue is deterministic`() {
        val q = listOf(PendingWidgetMutation("s1", WidgetMutationAction.MarkRead, 1_000))
        val a =
            PendingMutationQueue.enqueue(
                queue = q,
                mutation = PendingWidgetMutation("s2", WidgetMutationAction.MarkRead, 2_000),
            )
        val b =
            PendingMutationQueue.enqueue(
                queue = q,
                mutation = PendingWidgetMutation("s2", WidgetMutationAction.MarkRead, 2_000),
            )
        assertEquals(a, b)
    }

    @Test
    fun `blank summary id — input rejected, queue unchanged`() {
        // Defensive: a malformed App Group entry should not poison the
        // queue. The drain handler can rely on summaryId being non-blank.
        val q = listOf(PendingWidgetMutation("s1", WidgetMutationAction.MarkRead, 1_000))
        val out =
            PendingMutationQueue.enqueue(
                queue = q,
                mutation =
                    PendingWidgetMutation(
                        summaryId = "  ",
                        action = WidgetMutationAction.MarkRead,
                        createdAtMillis = 2_000,
                    ),
            )
        assertEquals(q, out)
    }
}
