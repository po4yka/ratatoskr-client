package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class ContinueReadingResolverTest {
    @Test
    fun `excludes summaries that have never been read`() {
        val items =
            listOf(
                summary("a", lastReadPosition = 0, createdAt = Instant.fromEpochSeconds(100)),
                summary("b", lastReadPosition = 5, createdAt = Instant.fromEpochSeconds(200)),
            )

        val shelf = ContinueReadingResolver.resolve(items)

        assertEquals(listOf("b"), shelf.map { it.id })
    }

    @Test
    fun `excludes summaries already marked read`() {
        // Regression guard: if a contributor forgets the !isRead clause the shelf becomes
        // "everything you've ever opened", which defeats the whole point of the rail.
        val items =
            listOf(
                summary("read-but-paused", lastReadPosition = 80, isRead = true),
                summary("partial", lastReadPosition = 20, isRead = false),
            )

        val shelf = ContinueReadingResolver.resolve(items)

        assertEquals(listOf("partial"), shelf.map { it.id })
    }

    @Test
    fun `sorts by recency descending`() {
        val items =
            listOf(
                summary("old", lastReadPosition = 10, createdAt = Instant.fromEpochSeconds(100)),
                summary("newest", lastReadPosition = 10, createdAt = Instant.fromEpochSeconds(300)),
                summary("middle", lastReadPosition = 10, createdAt = Instant.fromEpochSeconds(200)),
            )

        val shelf = ContinueReadingResolver.resolve(items)

        assertEquals(listOf("newest", "middle", "old"), shelf.map { it.id })
    }

    @Test
    fun `caps the shelf at MAX_ITEMS=8`() {
        // Regression guard for "older partial reads silently drop off" — the spec is explicit
        // that the rail must not become an unbounded list, which would push the rest of
        // SummaryListScreen offscreen on phones.
        val items =
            (1..12).map {
                summary(
                    id = "id-$it",
                    lastReadPosition = 5,
                    createdAt = Instant.fromEpochSeconds(it.toLong()),
                )
            }

        val shelf = ContinueReadingResolver.resolve(items)

        assertEquals(ContinueReadingResolver.MAX_ITEMS, shelf.size)
        assertEquals("id-12", shelf.first().id, "newest must survive the cap")
        assertEquals("id-5", shelf.last().id, "oldest 4 drop off")
    }

    @Test
    fun `archived summaries are excluded even when partially read`() {
        // The archive lane is explicitly "out of your way" — surfacing one in Continue would
        // be a UX bug where the user archives a summary but it boomerangs back to the top.
        val items =
            listOf(
                summary("active", lastReadPosition = 30),
                summary("archived", lastReadPosition = 40, isArchived = true),
            )

        val shelf = ContinueReadingResolver.resolve(items)

        assertEquals(listOf("active"), shelf.map { it.id })
    }

    @Test
    fun `empty input yields empty shelf — no awkward placeholder`() {
        // Spec: "Hidden when empty (no awkward 'Nothing to continue' empty state)".
        // The resolver guarantees the empty signal so the UI can `if (shelf.isEmpty()) return`.
        val shelf = ContinueReadingResolver.resolve(emptyList())

        assertTrue(shelf.isEmpty())
    }

    @Test
    fun `respects a caller-supplied recency extractor`() {
        // The Summary model does not yet have a `lastReadAt` field, so the default uses
        // createdAt as a proxy. When that field lands, callers can pass `it.lastReadAt`
        // without changing this algorithm. This test pins that contract.
        val a = summary("a", lastReadPosition = 10, createdAt = Instant.fromEpochSeconds(100))
        val b = summary("b", lastReadPosition = 10, createdAt = Instant.fromEpochSeconds(200))
        // Invert recency via the extractor — "a" should win because we tell the resolver so.
        val shelf =
            ContinueReadingResolver.resolve(
                items = listOf(a, b),
                recencyOf = { if (it.id == "a") Instant.fromEpochSeconds(999) else Instant.fromEpochSeconds(0) },
            )

        assertEquals(listOf("a", "b"), shelf.map { it.id })
    }

    private fun summary(
        id: String,
        lastReadPosition: Int = 0,
        isRead: Boolean = false,
        isArchived: Boolean = false,
        createdAt: Instant = Instant.fromEpochSeconds(0),
    ) = Summary(
        id = id,
        title = id,
        content = "",
        sourceUrl = "https://example.test/$id",
        imageUrl = null,
        createdAt = createdAt,
        isRead = isRead,
        tags = emptyList(),
        lastReadPosition = lastReadPosition,
        isArchived = isArchived,
    )
}
