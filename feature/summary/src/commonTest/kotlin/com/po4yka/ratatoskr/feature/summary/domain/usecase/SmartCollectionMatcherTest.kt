package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.presentation.state.DateRange
import com.po4yka.ratatoskr.presentation.state.SearchFilters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class SmartCollectionMatcherTest {
    @Test
    fun `empty query and no filters matches every summary`() {
        // Edge case: a "smart collection" saved with no query and the default filter
        // becomes a synonym for the full library. The matcher must not invent
        // restrictions the user never asked for.
        val items = listOf(summary("a"), summary("b", isRead = true))
        val result = SmartCollectionMatcher.match("", SearchFilters(), items)
        assertEquals(items, result)
    }

    @Test
    fun `query is case-insensitive substring match against title and content`() {
        val items =
            listOf(
                summary("hit-1", title = "Kafka deep dive"),
                summary("hit-2", title = "unrelated", content = "intro to kAfKa streams"),
                summary("miss", title = "Rust async", content = "tokio runtime"),
            )

        val result = SmartCollectionMatcher.match("kafka", SearchFilters(), items)

        assertEquals(setOf("hit-1", "hit-2"), result.map { it.id }.toSet())
    }

    @Test
    fun `query also searches fullContent when cached`() {
        // Regression guard: TLDR/content alone misses summaries where the keyword only
        // appears in the full article body. Smart collections should reflect the user's
        // mental model of "everything I have on this topic", including fullContent.
        val items =
            listOf(
                summary("in-full", title = "x", content = "y", fullContent = "deep notes on TLA+"),
                summary("not-anywhere", title = "x", content = "y"),
            )

        val result = SmartCollectionMatcher.match("TLA+", SearchFilters(), items)

        assertEquals(listOf("in-full"), result.map { it.id })
    }

    @Test
    fun `tag filter requires every tag to be present — AND semantics, not OR`() {
        // Regression guard: SearchFilters.tags is a list; the natural-but-wrong impl
        // would treat it as OR ("any of these tags"). The intent is intersection.
        val items =
            listOf(
                summary("both", tags = listOf("ml", "infra")),
                summary("only-ml", tags = listOf("ml")),
                summary("only-infra", tags = listOf("infra")),
            )

        val result =
            SmartCollectionMatcher.match(
                query = "",
                filters = SearchFilters(tags = listOf("ml", "infra")),
                summaries = items,
            )

        assertEquals(listOf("both"), result.map { it.id })
    }

    @Test
    fun `readFilter UNREAD excludes read and archived summaries`() {
        val items =
            listOf(
                summary("unread", isRead = false),
                summary("read", isRead = true),
                summary("archived-but-unread", isRead = false, isArchived = true),
            )

        val result =
            SmartCollectionMatcher.match(
                query = "",
                filters = SearchFilters(readFilter = ReadFilter.UNREAD),
                summaries = items,
            )

        assertEquals(listOf("unread"), result.map { it.id })
    }

    @Test
    fun `readFilter FAVORITED requires isFavorited=true regardless of read state`() {
        val items =
            listOf(
                summary("fav-read", isRead = true, isFavorited = true),
                summary("fav-unread", isRead = false, isFavorited = true),
                summary("not-fav", isFavorited = false),
            )

        val result =
            SmartCollectionMatcher.match(
                query = "",
                filters = SearchFilters(readFilter = ReadFilter.FAVORITED),
                summaries = items,
            )

        assertEquals(setOf("fav-read", "fav-unread"), result.map { it.id }.toSet())
    }

    @Test
    fun `dateRange filters by createdAt epoch millis — inclusive on both ends`() {
        val items =
            listOf(
                summary("before", createdAt = Instant.fromEpochMilliseconds(50)),
                summary("start-edge", createdAt = Instant.fromEpochMilliseconds(100)),
                summary("middle", createdAt = Instant.fromEpochMilliseconds(150)),
                summary("end-edge", createdAt = Instant.fromEpochMilliseconds(200)),
                summary("after", createdAt = Instant.fromEpochMilliseconds(250)),
            )

        val result =
            SmartCollectionMatcher.match(
                query = "",
                filters = SearchFilters(dateRange = DateRange(startDate = 100L, endDate = 200L)),
                summaries = items,
            )

        assertEquals(
            listOf("start-edge", "middle", "end-edge"),
            result.map { it.id },
            "boundary instants must be included — the user picked them deliberately",
        )
    }

    @Test
    fun `dateRange with only startDate set is treated as unbounded above`() {
        val items =
            listOf(
                summary("before", createdAt = Instant.fromEpochMilliseconds(50)),
                summary("after", createdAt = Instant.fromEpochMilliseconds(150)),
            )

        val result =
            SmartCollectionMatcher.match(
                query = "",
                filters = SearchFilters(dateRange = DateRange(startDate = 100L, endDate = null)),
                summaries = items,
            )

        assertEquals(listOf("after"), result.map { it.id })
    }

    @Test
    fun `predicates compose with AND — every clause must pass`() {
        // Regression guard for the most subtle bug class: a refactor that accidentally
        // returns true if *any* predicate passes. The matcher must intersect.
        val items =
            listOf(
                summary("perfect", title = "kafka talk", tags = listOf("conf"), isRead = false),
                summary("right-text-wrong-tag", title = "kafka talk", tags = listOf("blog"), isRead = false),
                summary("right-tag-wrong-text", title = "rust talk", tags = listOf("conf"), isRead = false),
                summary("everything-but-read", title = "kafka talk", tags = listOf("conf"), isRead = true),
            )

        val result =
            SmartCollectionMatcher.match(
                query = "kafka",
                filters = SearchFilters(tags = listOf("conf"), readFilter = ReadFilter.UNREAD),
                summaries = items,
            )

        assertEquals(listOf("perfect"), result.map { it.id })
    }

    @Test
    fun `empty input produces empty output`() {
        val result =
            SmartCollectionMatcher.match(
                query = "anything",
                filters = SearchFilters(tags = listOf("any")),
                summaries = emptyList(),
            )
        assertTrue(result.isEmpty())
    }

    private fun summary(
        id: String,
        title: String = id,
        content: String = "",
        fullContent: String? = null,
        tags: List<String> = emptyList(),
        isRead: Boolean = false,
        isArchived: Boolean = false,
        isFavorited: Boolean = false,
        createdAt: Instant = Instant.fromEpochSeconds(0),
    ) = Summary(
        id = id,
        title = title,
        content = content,
        sourceUrl = "https://example.test/$id",
        imageUrl = null,
        createdAt = createdAt,
        isRead = isRead,
        tags = tags,
        isFavorited = isFavorited,
        fullContent = fullContent,
        isArchived = isArchived,
    )
}
