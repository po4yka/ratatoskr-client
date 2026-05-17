package com.po4yka.ratatoskr.util.a11y

import kotlin.test.Test
import kotlin.test.assertEquals

class HeadingAnnounceLabelTest {
    @Test
    fun `level one renders the page-heading prefix`() {
        // Top-level page headings get a distinct "Page heading:" prefix
        // so TalkBack / VoiceOver users hear the screen anchor before
        // any sublevel content.
        assertEquals(
            "Page heading: Sync settings",
            HeadingAnnounceLabel.announce(text = "Sync settings", level = 1),
        )
    }

    @Test
    fun `levels two through six render the canonical level prefix`() {
        assertEquals(
            "Heading level 2, Section title",
            HeadingAnnounceLabel.announce(text = "Section title", level = 2),
        )
        assertEquals(
            "Heading level 3, Subsection",
            HeadingAnnounceLabel.announce(text = "Subsection", level = 3),
        )
        assertEquals(
            "Heading level 6, Detail row",
            HeadingAnnounceLabel.announce(text = "Detail row", level = 6),
        )
    }

    @Test
    fun `level below one clamps to one`() {
        // A caller bug passing 0 or -1 surfaces as "Page heading: ..."
        // rather than producing a malformed announce string like
        // "Heading level 0, ...".
        assertEquals(
            "Page heading: Title",
            HeadingAnnounceLabel.announce(text = "Title", level = 0),
        )
        assertEquals(
            "Page heading: Title",
            HeadingAnnounceLabel.announce(text = "Title", level = -1),
        )
    }

    @Test
    fun `level above six clamps to six`() {
        // HTML heading semantics top out at h6; anything beyond is
        // a caller bug. Clamp rather than introduce a level the OS
        // doesn't understand.
        assertEquals(
            "Heading level 6, Title",
            HeadingAnnounceLabel.announce(text = "Title", level = 7),
        )
        assertEquals(
            "Heading level 6, Title",
            HeadingAnnounceLabel.announce(text = "Title", level = 99),
        )
    }

    @Test
    fun `whitespace around the text is trimmed`() {
        // Compose `text` callsites can carry trailing newlines from
        // string-resource interpolation. Trim so screen readers don't
        // emit a stutter pause at the end.
        assertEquals(
            "Page heading: Title",
            HeadingAnnounceLabel.announce(text = "   Title   ", level = 1),
        )
    }

    @Test
    fun `empty or blank text returns an empty string`() {
        // An empty heading announce is worse than no announce — it
        // produces a phantom focus stop. Return empty so the caller
        // skips setting the semantics property.
        assertEquals("", HeadingAnnounceLabel.announce(text = "", level = 1))
        assertEquals("", HeadingAnnounceLabel.announce(text = "   ", level = 2))
    }

    @Test
    fun `decision is deterministic`() {
        val a = HeadingAnnounceLabel.announce(text = "Section", level = 3)
        val b = HeadingAnnounceLabel.announce(text = "Section", level = 3)
        assertEquals(a, b)
    }

    @Test
    fun `level prefix never embeds the raw level integer outside the canonical band`() {
        // Pin: even with garbage input, the output level digit is
        // always 1..6. Protects against the announce string leaking
        // a raw caller-supplied integer that wasn't clamped.
        val outOfBand = listOf(-10, -1, 0, 7, 99, Int.MAX_VALUE)
        for (level in outOfBand) {
            val out = HeadingAnnounceLabel.announce(text = "Title", level = level)
            val containsForbiddenLevel =
                (-10..0).any { out.contains("Heading level $it") } ||
                    (7..100).any { out.contains("Heading level $it") }
            assertEquals(false, containsForbiddenLevel, "out-of-band level leaked: $out")
        }
    }
}
