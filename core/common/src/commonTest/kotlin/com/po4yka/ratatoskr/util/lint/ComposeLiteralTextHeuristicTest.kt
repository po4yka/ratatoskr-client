package com.po4yka.ratatoskr.util.lint

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComposeLiteralTextHeuristicTest {
    @Test
    fun `single-word capitalized button label flags as user-facing — Save`() {
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Save"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Submit"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Done"))
    }

    @Test
    fun `multi-word prose with whitespace flags as user-facing`() {
        // Any literal with whitespace is almost certainly prose, a sentence,
        // or a multi-word label — never a test tag or resource key.
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Submit URL"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Sign in with Telegram"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Couldn't copy"))
    }

    @Test
    fun `format placeholders flag as user-facing — count strings drive plural migration`() {
        // The original spec calls out count-driven strings using %1$d. The
        // heuristic must flag these so the migration pass to
        // pluralStringResource picks them up.
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("%1\$d items"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("%1\$d-day streak"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Target: %1\$d minutes per day"))
    }

    @Test
    fun `snake_case identifier does not flag — these are resource keys and test tags`() {
        // The widest false-positive risk: detekt fires on every literal, and
        // testTag("summary_list_root") must not be flagged. snake_case is a
        // strong identifier signal.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("summary_list_root"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("screen_title_save"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("a11y_submit_button"))
    }

    @Test
    fun `kebab-case does not flag — slugs and config keys`() {
        // URL slugs, route paths, and config keys use kebab-case. Reading
        // "submit-url-form" aloud to a user as a label is nonsense; the
        // rule should not surface it.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("submit-url-form"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("reading-list"))
    }

    @Test
    fun `camelCase identifier does not flag — Compose testTag conventions`() {
        // Compose tests routinely use camelCase tags. The first character is
        // lowercase, the rest are letters with no separator — definitively an
        // identifier shape.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("submitButton"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("fabContainer"))
    }

    @Test
    fun `dot notation does not flag — qualified identifiers and URLs`() {
        // Property keys, qualified class names, URLs — all use dots.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("feature.summary.title"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("ratatoskr.client.v1"))
    }

    @Test
    fun `URL-shaped literals do not flag — likely passed to ExternalUrlOpener, not displayed`() {
        // We already have the URL-opener pattern; URLs in Compose calls are
        // almost always navigation targets, not labels.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("https://example.com/path"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("ratatoskr://summary/x"))
    }

    @Test
    fun `empty and short literals do not flag — separators and single-char tokens`() {
        // Length 0 or 1 is almost never user-facing prose. Length-2 single
        // tokens like "OK" / "Hi" are a judgment call — the rule keeps the
        // threshold strict at 1 to avoid false negatives on real labels.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText(""))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText(" "))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("x"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("1"))
    }

    @Test
    fun `short uppercase labels still flag as user-facing — OK is a real UI label`() {
        // "OK" is the canonical confirm-dialog label. The heuristic must
        // flag it so a literal Text("OK") gets migrated to a stringResource.
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("OK"))
    }

    @Test
    fun `punctuation in prose is preserved as user-facing — apostrophes and commas`() {
        // "Couldn't copy" already covered; add commas, exclamation, question.
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Hello, world!"))
        assertTrue(ComposeLiteralTextHeuristic.looksLikeUserText("Are you sure?"))
    }

    @Test
    fun `pure-numeric and symbol-only literals do not flag`() {
        // Numeric constants in widgets ("10:30", "•") aren't prose. The
        // heuristic should pass these through silently.
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("123"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("•"))
        assertFalse(ComposeLiteralTextHeuristic.looksLikeUserText("|"))
    }
}
