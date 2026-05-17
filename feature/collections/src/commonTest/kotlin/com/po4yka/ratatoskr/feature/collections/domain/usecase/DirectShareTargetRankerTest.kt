package com.po4yka.ratatoskr.feature.collections.domain.usecase

import com.po4yka.ratatoskr.feature.collections.domain.usecase.DirectShareTargetRanker.UsageEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class DirectShareTargetRankerTest {
    @Test
    fun `empty input yields empty output`() {
        val targets = DirectShareTargetRanker.rank(events = emptyList())
        assertTrue(targets.isEmpty(), "no usage history means no direct-share targets to seed")
    }

    @Test
    fun `single use of one collection ranks it first`() {
        val targets =
            DirectShareTargetRanker.rank(
                events = listOf(use("reading-list", at = 100)),
            )
        assertEquals(listOf("reading-list"), targets)
    }

    @Test
    fun `repeated uses of the same collection dedupe — one entry, latest timestamp wins`() {
        // Regression guard: a naive impl that takes the first event per id would
        // anchor the ordering at the *oldest* use rather than the latest. The seeder
        // refreshes shortcuts after each use; the latest event is the relevant signal.
        val targets =
            DirectShareTargetRanker.rank(
                events =
                    listOf(
                        use("reading-list", at = 100),
                        use("reading-list", at = 200),
                        use("reading-list", at = 300),
                    ),
            )
        assertEquals(listOf("reading-list"), targets)
    }

    @Test
    fun `ordering is by most-recent use descending`() {
        val targets =
            DirectShareTargetRanker.rank(
                events =
                    listOf(
                        use("oldest", at = 100),
                        use("middle", at = 200),
                        use("newest", at = 300),
                    ),
            )
        assertEquals(listOf("newest", "middle", "oldest"), targets)
    }

    @Test
    fun `re-using an older collection moves it to the front — true LRU behavior`() {
        // The spec calls out "Re-seed shortcuts after each successful collection use
        // (LRU)" — the user just used "work", so on next share-sheet open it must
        // appear ahead of "reading-list" which they last used earlier.
        val targets =
            DirectShareTargetRanker.rank(
                events =
                    listOf(
                        use("reading-list", at = 100),
                        use("work", at = 50),
                        use("work", at = 200), // re-used after reading-list
                    ),
            )
        assertEquals(listOf("work", "reading-list"), targets)
    }

    @Test
    fun `cap at 4 distinct collections to respect Android shortcut limits`() {
        // Spec: "Cap at 4 shortcuts to respect Android limits." A 5th distinct
        // collection's events must be filtered out even if all are recent.
        val targets =
            DirectShareTargetRanker.rank(
                events = (1..6).map { use("c-$it", at = (100 + it).toLong()) },
            )
        assertEquals(DirectShareTargetRanker.MAX_TARGETS, targets.size)
        assertEquals(listOf("c-6", "c-5", "c-4", "c-3"), targets, "newest four survive the cap")
    }

    @Test
    fun `cap is overridable for unit-test isolation and future watch-companion seeding`() {
        // Future use case: a watch face has tighter shortcut budget. The cap is a
        // parameter so the algorithm doesn't need a global config.
        val targets =
            DirectShareTargetRanker.rank(
                events = listOf(use("a", 100), use("b", 200), use("c", 300)),
                maxTargets = 2,
            )
        assertEquals(listOf("c", "b"), targets)
    }

    @Test
    fun `events arriving in arbitrary order produce the same ranking — order-independence`() {
        // Defends against a refactor that accidentally relies on input pre-sort.
        // The ranker must compute the same result whatever order events arrive in.
        val ordered =
            DirectShareTargetRanker.rank(
                events = listOf(use("a", 100), use("b", 200), use("c", 300)),
            )
        val shuffled =
            DirectShareTargetRanker.rank(
                events = listOf(use("c", 300), use("a", 100), use("b", 200)),
            )
        assertEquals(ordered, shuffled)
    }

    @Test
    fun `dedupe survives interleaved usage of multiple collections`() {
        // Regression guard for the most common bug class: id-level dedupe must
        // consider the latest timestamp across *all* events for an id, even when
        // those events are interleaved with other collections' events.
        val targets =
            DirectShareTargetRanker.rank(
                events =
                    listOf(
                        use("a", 100),
                        use("b", 150),
                        use("a", 400), // a re-used latest
                        use("c", 200),
                        use("b", 350),
                    ),
            )
        assertEquals(listOf("a", "b", "c"), targets)
    }

    private fun use(
        id: String,
        at: Long,
    ) = UsageEvent(collectionId = id, usedAt = Instant.fromEpochSeconds(at))
}
