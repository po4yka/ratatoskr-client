package com.po4yka.ratatoskr.util.a11y

import kotlin.test.Test
import kotlin.test.assertEquals

class ListItemAnnounceTest {
    @Test
    fun `canonical announce format`() {
        // Pin the exact phrase TalkBack / VoiceOver expects so a
        // refactor doesn't accidentally swap the positions of N and M
        // (a real bug in earlier versions of Compose accessibility
        // helpers).
        assertEquals(
            "Item 3 of 10, Article title",
            ListItemAnnounce.announce(
                indexOneBased = 3,
                totalCount = 10,
                itemText = "Article title",
            ),
        )
    }

    @Test
    fun `index is reported as one-based — first item is 1 not 0`() {
        assertEquals(
            "Item 1 of 5, First item",
            ListItemAnnounce.announce(
                indexOneBased = 1,
                totalCount = 5,
                itemText = "First item",
            ),
        )
    }

    @Test
    fun `index zero is clamped to one — defensive against 0-based caller`() {
        // A caller passing the zero-based index is a common bug; the
        // announce string must not say "Item 0 of N".
        assertEquals(
            "Item 1 of 5, x",
            ListItemAnnounce.announce(
                indexOneBased = 0,
                totalCount = 5,
                itemText = "x",
            ),
        )
    }

    @Test
    fun `index above total is clamped to total`() {
        // A racing scroll-position calculation can briefly produce
        // index > total. Clamp rather than announce a position
        // beyond the list end.
        assertEquals(
            "Item 5 of 5, x",
            ListItemAnnounce.announce(
                indexOneBased = 99,
                totalCount = 5,
                itemText = "x",
            ),
        )
    }

    @Test
    fun `total of zero — degenerate but never crash`() {
        // A list with zero items shouldn't announce items at all,
        // but if a caller still invokes the helper, return a clean
        // empty string rather than crash.
        assertEquals(
            "",
            ListItemAnnounce.announce(
                indexOneBased = 1,
                totalCount = 0,
                itemText = "x",
            ),
        )
    }

    @Test
    fun `negative index is clamped to one`() {
        assertEquals(
            "Item 1 of 5, x",
            ListItemAnnounce.announce(
                indexOneBased = -3,
                totalCount = 5,
                itemText = "x",
            ),
        )
    }

    @Test
    fun `negative total is treated as zero — degenerate`() {
        assertEquals(
            "",
            ListItemAnnounce.announce(
                indexOneBased = 1,
                totalCount = -5,
                itemText = "x",
            ),
        )
    }

    @Test
    fun `item text is trimmed`() {
        assertEquals(
            "Item 2 of 5, Trimmed",
            ListItemAnnounce.announce(
                indexOneBased = 2,
                totalCount = 5,
                itemText = "  Trimmed  ",
            ),
        )
    }

    @Test
    fun `blank item text — announce the position without trailing comma noise`() {
        // Even an empty item label should announce its list position
        // so a screen-reader user knows they reached a row, not a
        // phantom focus stop.
        assertEquals(
            "Item 2 of 5",
            ListItemAnnounce.announce(
                indexOneBased = 2,
                totalCount = 5,
                itemText = "",
            ),
        )
        assertEquals(
            "Item 2 of 5",
            ListItemAnnounce.announce(
                indexOneBased = 2,
                totalCount = 5,
                itemText = "   ",
            ),
        )
    }

    @Test
    fun `announce is deterministic`() {
        val a =
            ListItemAnnounce.announce(
                indexOneBased = 3,
                totalCount = 10,
                itemText = "x",
            )
        val b =
            ListItemAnnounce.announce(
                indexOneBased = 3,
                totalCount = 10,
                itemText = "x",
            )
        assertEquals(a, b)
    }
}
