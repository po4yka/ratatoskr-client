package com.po4yka.ratatoskr.util.markdown

/**
 * Escapes a single value for safe inclusion in a markdown table cell.
 * Used by the export pipeline (`SummaryMarkdownExporter`) when it
 * renders summary metadata as a table.
 *
 * Two characters are markdown-meaningful inside a table cell:
 *  - `|` is the column delimiter; a literal pipe in cell content
 *    breaks the row layout in most renderers (GitHub, Obsidian, GFM)
 *    even when the surrounding `|` columns are unambiguous.
 *  - Newlines (LF / CRLF / bare CR) terminate the row; the canonical
 *    workaround is `<br>`, which all the renderers we target accept.
 *
 * The escape set is deliberately minimal — backslashes survive
 * untouched so that Windows paths and code blocks in cells render
 * naturally. This is a one-way transformation; the result is not
 * intended to round-trip back to the original through the same
 * function.
 *
 * Null and empty inputs collapse to the empty string so the caller
 * does not have to branch on nullability before joining the row.
 */
object MarkdownTableEscaper {
    fun escapeCell(text: String?): String {
        if (text.isNullOrEmpty()) return ""
        return text
            .replace("\r\n", "<br>")
            .replace("\n", "<br>")
            .replace("\r", "<br>")
            .replace("|", "\\|")
    }
}
