package com.po4yka.ratatoskr.util.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownTableEscaperTest {
    @Test
    fun `null and empty inputs collapse to empty string`() {
        assertEquals("", MarkdownTableEscaper.escapeCell(null))
        assertEquals("", MarkdownTableEscaper.escapeCell(""))
    }

    @Test
    fun `plain text without special characters passes through`() {
        assertEquals("Reading Notes 2026", MarkdownTableEscaper.escapeCell("Reading Notes 2026"))
    }

    @Test
    fun `pipe is escaped to backslash-pipe`() {
        // A literal pipe inside a markdown table cell breaks the table —
        // most renderers interpret it as the column boundary even inside
        // an "obvious" cell context.
        assertEquals("foo \\| bar", MarkdownTableEscaper.escapeCell("foo | bar"))
        assertEquals("a\\|b\\|c", MarkdownTableEscaper.escapeCell("a|b|c"))
    }

    @Test
    fun `LF newline is replaced with HTML break tag`() {
        // Markdown table cells don't render literal newlines — each
        // newline ends the row. The canonical workaround is to insert
        // an HTML <br> which most renderers (GitHub, Obsidian, CommonMark
        // GFM) accept inside table cells.
        assertEquals("line 1<br>line 2", MarkdownTableEscaper.escapeCell("line 1\nline 2"))
    }

    @Test
    fun `CRLF newline is replaced with a single break tag`() {
        // Windows-formatted summaries arrive with CRLF — the two-byte
        // sequence must collapse to one <br>, not two.
        assertEquals("line 1<br>line 2", MarkdownTableEscaper.escapeCell("line 1\r\nline 2"))
    }

    @Test
    fun `bare CR is replaced with break tag`() {
        // Classic Mac-style CR-only line endings still surface from
        // some clipboard pastes; the escape handles them too.
        assertEquals("line 1<br>line 2", MarkdownTableEscaper.escapeCell("line 1\rline 2"))
    }

    @Test
    fun `multiple consecutive newlines each become a break tag`() {
        // Each newline preserves its visual break; the consumer (Obsidian
        // table renderer) collapses runs of <br> as it sees fit.
        assertEquals("a<br><br>b", MarkdownTableEscaper.escapeCell("a\n\nb"))
    }

    @Test
    fun `pipe and newline mix correctly`() {
        // Real-world cells often contain both — pin the combined order.
        val raw = "foo|bar\nbaz|qux"
        val expected = "foo\\|bar<br>baz\\|qux"
        assertEquals(expected, MarkdownTableEscaper.escapeCell(raw))
    }

    @Test
    fun `backslash itself is not escaped — only pipe is`() {
        // A literal backslash in a cell renders as-is in markdown. We
        // deliberately do not escape it: doing so would break common
        // content like Windows file paths. The escape contract is the
        // minimal set that fixes table parsing.
        assertEquals("C:\\Users\\foo", MarkdownTableEscaper.escapeCell("C:\\Users\\foo"))
    }

    @Test
    fun `Unicode characters survive unchanged`() {
        // No filtering on script — only the two markdown-meaningful
        // characters are transformed.
        assertEquals("Заметки 2026", MarkdownTableEscaper.escapeCell("Заметки 2026"))
        assertEquals("笔记 一月", MarkdownTableEscaper.escapeCell("笔记 一月"))
    }
}
