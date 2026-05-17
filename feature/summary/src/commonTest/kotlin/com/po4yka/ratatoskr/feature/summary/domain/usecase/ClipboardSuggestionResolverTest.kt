package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.feature.summary.domain.usecase.ClipboardSuggestionResolver.Decision
import com.po4yka.ratatoskr.feature.summary.domain.usecase.ClipboardSuggestionResolver.HideReason
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClipboardSuggestionResolverTest {
    @Test
    fun `hide when clipboard does not contain a URL`() {
        val d =
            ClipboardSuggestionResolver.resolve(
                hasClipboardUrl = false,
                urlIfKnown = null,
                recentlyDismissed = emptySet(),
                libraryContains = false,
            )
        assertEquals(Decision.Hide(HideReason.NO_URL_ON_CLIPBOARD), d)
    }

    @Test
    fun `show with null url on iOS path when probe says there is one`() {
        // iOS probe returns Boolean only — URL is unknown until readUrl().
        val d =
            ClipboardSuggestionResolver.resolve(
                hasClipboardUrl = true,
                urlIfKnown = null,
                recentlyDismissed = setOf("https://example.com/a"),
                libraryContains = false,
            )
        assertIs<Decision.Show>(d)
        assertNull(d.urlIfKnown)
    }

    @Test
    fun `show with truncatable url on Android path`() {
        val d =
            ClipboardSuggestionResolver.resolve(
                hasClipboardUrl = true,
                urlIfKnown = "https://example.com/article-42",
                recentlyDismissed = emptySet(),
                libraryContains = false,
            )
        assertEquals(Decision.Show(urlIfKnown = "https://example.com/article-42"), d)
    }

    @Test
    fun `hide when the clipboard url is already in the library`() {
        val d =
            ClipboardSuggestionResolver.resolve(
                hasClipboardUrl = true,
                urlIfKnown = "https://example.com/article-42",
                recentlyDismissed = emptySet(),
                libraryContains = true,
            )
        assertEquals(Decision.Hide(HideReason.ALREADY_IN_LIBRARY), d)
    }

    @Test
    fun `hide when the url was dismissed earlier in this session`() {
        val d =
            ClipboardSuggestionResolver.resolve(
                hasClipboardUrl = true,
                urlIfKnown = "https://example.com/article-42",
                recentlyDismissed = setOf("https://example.com/article-42"),
                libraryContains = false,
            )
        assertEquals(Decision.Hide(HideReason.RECENTLY_DISMISSED), d)
    }

    @Test
    fun `library check wins over dismissal check`() {
        // A summary that's both dismissed and in the library should hide as
        // ALREADY_IN_LIBRARY — the more accurate reason for telemetry.
        val d =
            ClipboardSuggestionResolver.resolve(
                hasClipboardUrl = true,
                urlIfKnown = "https://example.com/article-42",
                recentlyDismissed = setOf("https://example.com/article-42"),
                libraryContains = true,
            )
        assertEquals(Decision.Hide(HideReason.ALREADY_IN_LIBRARY), d)
    }

    @Test
    fun `appending a new url to an empty list grows it to size one`() {
        val out = ClipboardSuggestionResolver.appendDismissed(emptyList(), "https://a")
        assertEquals(listOf("https://a"), out)
    }

    @Test
    fun `appending a duplicate moves it to the most-recent slot rather than growing the list`() {
        val seed = listOf("https://a", "https://b", "https://c")
        val out = ClipboardSuggestionResolver.appendDismissed(seed, "https://a")
        assertEquals(listOf("https://b", "https://c", "https://a"), out)
    }

    @Test
    fun `appending past the dismissal window drops the oldest entries`() {
        // Build a 16-element list, then add a 17th.
        val seed = (1..ClipboardSuggestionResolver.DISMISSAL_WINDOW).map { "https://example.com/$it" }
        val out = ClipboardSuggestionResolver.appendDismissed(seed, "https://example.com/17")

        assertEquals(ClipboardSuggestionResolver.DISMISSAL_WINDOW, out.size)
        assertTrue("https://example.com/17" in out, "newest entry must remain")
        assertTrue("https://example.com/1" !in out, "oldest entry must be evicted")
    }
}
