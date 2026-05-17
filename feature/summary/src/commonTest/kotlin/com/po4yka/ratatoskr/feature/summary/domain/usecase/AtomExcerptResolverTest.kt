package com.po4yka.ratatoskr.feature.summary.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AtomExcerptResolverTest {
    @Test
    fun `exact substring is the highest-priority match`() {
        val full = "The dam was reinforced in 1953. Local authorities maintain it monthly."
        val fact = "The dam was reinforced in 1953."

        val match = AtomExcerptResolver.resolve(factText = fact, fullContent = full)

        assertNotNull(match)
        assertEquals(0, match.startOffset)
        assertEquals(fact.length, match.endOffset)
        assertEquals(AtomExcerptResolver.MatchStrategy.EXACT, match.strategy)
    }

    @Test
    fun `case-insensitive match catches casing drift between the atom and the article`() {
        // Regression guard: the backend often title-cases or sentence-cases newFacts.
        // A strict exact match would miss the obvious connection between
        // "Dam was reinforced in 1953" and "the dam was reinforced in 1953".
        val full = "The dam was reinforced in 1953."
        val fact = "Dam Was Reinforced In 1953"

        val match = AtomExcerptResolver.resolve(factText = fact, fullContent = full)

        assertNotNull(match)
        assertEquals(AtomExcerptResolver.MatchStrategy.CASE_INSENSITIVE, match.strategy)
        // The snippet preserves the article's original casing — that's what gets highlighted.
        assertEquals("dam was reinforced in 1953", full.substring(match.startOffset, match.endOffset))
    }

    @Test
    fun `fuzzy fallback finds the sentence with the highest token overlap`() {
        // When the fact is a paraphrase ("by 25 percent") and the article has the
        // numbers and unit but different syntax ("a 25% increase"), the fuzzy
        // strategy must still surface the right sentence.
        val full =
            """
            The team reported a 25% increase in throughput last quarter.
            Costs fell during the same period.
            Engineering hiring slowed.
            """.trimIndent()
        val fact = "throughput rose 25 percent"

        val match = AtomExcerptResolver.resolve(factText = fact, fullContent = full)

        assertNotNull(match)
        assertEquals(AtomExcerptResolver.MatchStrategy.FUZZY_SENTENCE, match.strategy)
        val snippet = full.substring(match.startOffset, match.endOffset)
        assertTrue(snippet.contains("25%"), "best-overlap sentence is the throughput line")
        assertTrue(snippet.contains("throughput"))
    }

    @Test
    fun `fuzzy fallback returns null when no sentence clears the minimum overlap threshold`() {
        // Defends against false positives. If the atom is unrelated to the article
        // (which would be a backend bug), the resolver returns null and the UI
        // surfaces no clickable affordance instead of jumping to a random sentence.
        val full = "Recipes for sourdough bread vary by region."
        val fact = "quantum chromodynamics renormalization"

        val match = AtomExcerptResolver.resolve(factText = fact, fullContent = full)

        assertNull(match)
    }

    @Test
    fun `null or blank fullContent yields a null match — no clickable atom`() {
        // Spec: "Works offline once the summary is cached." Before fullContent loads,
        // the atom must not be clickable, which means the resolver must signal that.
        assertNull(AtomExcerptResolver.resolve(factText = "anything", fullContent = null))
        assertNull(AtomExcerptResolver.resolve(factText = "anything", fullContent = ""))
        assertNull(AtomExcerptResolver.resolve(factText = "anything", fullContent = "   "))
    }

    @Test
    fun `blank factText yields a null match — defensive against empty atoms`() {
        assertNull(AtomExcerptResolver.resolve(factText = "", fullContent = "Some article body"))
        assertNull(AtomExcerptResolver.resolve(factText = "   ", fullContent = "Some article body"))
    }

    @Test
    fun `match offsets index into the original fullContent, not a normalized copy`() {
        // Regression guard: the UI uses startOffset to scroll. If the resolver
        // normalized whitespace or stripped punctuation, the offsets it returns
        // would index into the wrong string and the scroll target would drift.
        val full = "  Sentence one.\n\n  The dam was reinforced.  Sentence three."
        val fact = "The dam was reinforced"

        val match = AtomExcerptResolver.resolve(factText = fact, fullContent = full)

        assertNotNull(match)
        assertEquals(fact, full.substring(match.startOffset, match.endOffset))
    }

    @Test
    fun `exact strategy beats fuzzy even when fuzzy would find a longer snippet`() {
        // Regression guard for priority ordering. The EXACT match here is a short
        // phrase; a fuzzy match would prefer the longer surrounding sentence. The
        // contract is that exact wins — the snippet is the user-facing receipt
        // that the atom truly came from this exact wording.
        val full = "The bridge collapsed. The bridge collapsed in 1976 after heavy flooding."
        val fact = "The bridge collapsed"

        val match = AtomExcerptResolver.resolve(factText = fact, fullContent = full)

        assertNotNull(match)
        assertEquals(AtomExcerptResolver.MatchStrategy.EXACT, match.strategy)
    }
}
