package com.po4yka.ratatoskr.feature.summary.export

import com.po4yka.ratatoskr.domain.model.InsightFact
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.model.SummaryInsights
import com.po4yka.ratatoskr.domain.model.SummaryQuality
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class SummaryMarkdownExporterTest {
    private fun fixedSummary(
        id: String = "smr_001",
        title: String = "How Ratatoskr handles offline pending operations",
        content: String = "The sync repository drains pending ops on reconnect.",
        sourceUrl: String = "https://example.com/blog/ratatoskr-sync",
        tags: List<String> = listOf("kmp", "sync"),
    ): Summary =
        Summary(
            id = id,
            title = title,
            content = content,
            sourceUrl = sourceUrl,
            imageUrl = null,
            createdAt = Instant.parse("2026-04-01T12:00:00Z"),
            isRead = false,
            tags = tags,
            readingTimeMin = 4,
        )

    @Test
    fun `markdown contains yaml frontmatter with title source created tags`() {
        val md = SummaryMarkdownExporter.toMarkdown(fixedSummary())

        assertTrue(md.startsWith("---\n"), "frontmatter must open on first line: $md")
        assertTrue(md.contains("title: \"How Ratatoskr handles offline pending operations\""))
        assertTrue(md.contains("source: \"https://example.com/blog/ratatoskr-sync\""))
        assertTrue(md.contains("created: \"2026-04-01T12:00:00Z\""))
        assertTrue(md.contains("tags: [kmp, sync]"))
        assertTrue(md.contains("---\n\n# How Ratatoskr handles offline pending operations"))
        assertTrue(md.contains("\nThe sync repository drains pending ops on reconnect.\n"))
    }

    @Test
    fun `export is deterministic`() {
        val s = fixedSummary()
        val a = SummaryMarkdownExporter.toMarkdown(s)
        val b = SummaryMarkdownExporter.toMarkdown(s)
        assertEquals(a, b)
    }

    @Test
    fun `empty tags renders as empty array`() {
        val md = SummaryMarkdownExporter.toMarkdown(fixedSummary(tags = emptyList()))
        assertTrue(md.contains("tags: []"), "expected empty tags array: $md")
    }

    @Test
    fun `title with double quotes is escaped`() {
        val md =
            SummaryMarkdownExporter.toMarkdown(
                fixedSummary(title = "Don't say \"never\" in production"),
            )
        // YAML double-quoted scalar requires backslash-escaping for inner quotes.
        assertTrue(
            md.contains("""title: "Don't say \"never\" in production""""),
            "expected escaped title in: $md",
        )
    }

    @Test
    fun `title with backslash is escaped`() {
        val md =
            SummaryMarkdownExporter.toMarkdown(
                fixedSummary(title = "C:\\Users\\path"),
            )
        assertTrue(
            md.contains("""title: "C:\\Users\\path""""),
            "expected escaped backslashes in: $md",
        )
    }

    @Test
    fun `insights atoms render as bullet list`() {
        val summary =
            fixedSummary().copy(
                insights =
                    SummaryInsights(
                        newFacts =
                            listOf(
                                InsightFact(fact = "Sync resumes from cursor"),
                                InsightFact(fact = "Conflicts surfaced via PendingOperation"),
                            ),
                    ),
            )
        val md = SummaryMarkdownExporter.toMarkdown(summary)
        assertTrue(md.contains("\n## Atoms\n"))
        assertTrue(md.contains("\n- Sync resumes from cursor\n"))
        assertTrue(md.contains("\n- Conflicts surfaced via PendingOperation\n"))
    }

    @Test
    fun `full content preferred over summary when cached`() {
        val summary =
            fixedSummary().copy(
                content = "short tldr",
                fullContent = "Full article body with several paragraphs.",
                isFullContentCached = true,
            )
        val md = SummaryMarkdownExporter.toMarkdown(summary)
        assertTrue(md.contains("## TLDR\n\nshort tldr"))
        assertTrue(md.contains("## Full content\n\nFull article body with several paragraphs."))
    }

    @Test
    fun `quality and confidence emitted when present`() {
        val summary =
            fixedSummary().copy(
                confidence = 0.82,
                quality = SummaryQuality(authorBias = "moderate", evidenceQuality = "primary"),
            )
        val md = SummaryMarkdownExporter.toMarkdown(summary)
        assertTrue(md.contains("confidence: 0.82"))
        assertTrue(md.contains("quality.author_bias: \"moderate\""))
        assertTrue(md.contains("quality.evidence_quality: \"primary\""))
    }

    @Test
    fun `pipe in title does not break frontmatter`() {
        // We use double-quoted YAML scalars, so pipe characters never trigger
        // block-scalar parsing.
        val md = SummaryMarkdownExporter.toMarkdown(fixedSummary(title = "Pipes | inside | titles"))
        assertFalse(md.contains("\ntitle: |"), "must not produce block scalar: $md")
        assertTrue(md.contains("title: \"Pipes | inside | titles\""))
    }
}
