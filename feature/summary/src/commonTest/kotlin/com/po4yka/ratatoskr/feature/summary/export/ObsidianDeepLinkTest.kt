package com.po4yka.ratatoskr.feature.summary.export

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObsidianDeepLinkTest {
    @Test
    fun `compose builds new-note url with vault name and content`() {
        val link =
            ObsidianDeepLink.composeNewNote(
                vault = "ratatoskr",
                name = "Sync internals",
                content = "## Title\nbody",
            )
        assertTrue(link.startsWith("obsidian://new?"))
        // Order is stable: vault, name, content.
        assertEquals(
            "obsidian://new?vault=ratatoskr&name=Sync%20internals&content=%23%23%20Title%0Abody",
            link,
        )
    }

    @Test
    fun `compose omits vault when blank so obsidian picks last-used vault`() {
        val link =
            ObsidianDeepLink.composeNewNote(
                vault = "",
                name = "n",
                content = "c",
            )
        assertFalse(link.contains("vault="), "blank vault must be omitted: $link")
        assertTrue(link.startsWith("obsidian://new?"))
    }

    @Test
    fun `compose percent-encodes ampersand and equals in vault name and content`() {
        // Ampersand and equals must be percent-encoded — otherwise they would
        // split the query string.
        val link =
            ObsidianDeepLink.composeNewNote(
                vault = "team&ops",
                name = "a=b",
                content = "x&y",
            )
        assertTrue(link.contains("vault=team%26ops"), "ampersand encoded: $link")
        assertTrue(link.contains("name=a%3Db"), "equals encoded: $link")
        assertTrue(link.contains("content=x%26y"), "content ampersand encoded: $link")
    }

    @Test
    fun `compose percent-encodes plus sign and space distinctly`() {
        // RFC 3986 percent-encoding: space is %20, plus stays literal '+' or
        // becomes %2B — never the form-encoded space ' '.
        val link =
            ObsidianDeepLink.composeNewNote(
                vault = "v",
                name = "1 + 1",
                content = " ",
            )
        // Use %20 for space (Obsidian parses application/x-www-form-urlencoded
        // permissively but %20 is the canonical URI encoding).
        assertTrue(link.contains("name=1%20%2B%201"), "plus and space encoded: $link")
        assertTrue(link.contains("content=%20"), "single-space content: $link")
    }

    @Test
    fun `compose percent-encodes non-ascii chars as utf8 bytes`() {
        val link =
            ObsidianDeepLink.composeNewNote(
                vault = "v",
                name = "café",
                content = "",
            )
        assertTrue(link.contains("name=caf%C3%A9"), "UTF-8 bytes for non-ASCII: $link")
    }

    @Test
    fun `compose is deterministic`() {
        val a = ObsidianDeepLink.composeNewNote("v", "n", "c")
        val b = ObsidianDeepLink.composeNewNote("v", "n", "c")
        assertEquals(a, b)
    }
}
