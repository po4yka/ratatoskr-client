package com.po4yka.ratatoskr.util.export

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExportFormatNegotiatorTest {
    @Test
    fun `markdown mime types map to Markdown`() {
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromMime("text/markdown"))
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromMime("text/x-markdown"))
    }

    @Test
    fun `markdown filename extensions map to Markdown`() {
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromExtension("notes.md"))
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromExtension("notes.markdown"))
    }

    @Test
    fun `html mime and extension map to Html`() {
        assertEquals(ExportFormat.Html, ExportFormatNegotiator.fromMime("text/html"))
        assertEquals(ExportFormat.Html, ExportFormatNegotiator.fromExtension("page.html"))
        assertEquals(ExportFormat.Html, ExportFormatNegotiator.fromExtension("page.htm"))
    }

    @Test
    fun `pdf mime and extension map to Pdf`() {
        assertEquals(ExportFormat.Pdf, ExportFormatNegotiator.fromMime("application/pdf"))
        assertEquals(ExportFormat.Pdf, ExportFormatNegotiator.fromExtension("doc.pdf"))
    }

    @Test
    fun `plain text mime and extension map to PlainText`() {
        assertEquals(ExportFormat.PlainText, ExportFormatNegotiator.fromMime("text/plain"))
        assertEquals(ExportFormat.PlainText, ExportFormatNegotiator.fromExtension("doc.txt"))
    }

    @Test
    fun `mime negotiation is case-insensitive`() {
        // Some Android intent receivers uppercase MIME parts.
        assertEquals(ExportFormat.Pdf, ExportFormatNegotiator.fromMime("APPLICATION/PDF"))
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromMime("Text/MarkDown"))
    }

    @Test
    fun `extension lookup is case-insensitive`() {
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromExtension("notes.MD"))
        assertEquals(ExportFormat.Pdf, ExportFormatNegotiator.fromExtension("doc.PDF"))
    }

    @Test
    fun `mime parameters are stripped before lookup`() {
        // `application/pdf; charset=utf-8` is a legal MIME shape; pin
        // that the negotiator splits on `;` and trims so the parameter
        // doesn't defeat the lookup.
        assertEquals(
            ExportFormat.Pdf,
            ExportFormatNegotiator.fromMime("application/pdf; charset=utf-8"),
        )
        assertEquals(
            ExportFormat.Markdown,
            ExportFormatNegotiator.fromMime("text/markdown ; charset=ascii"),
        )
    }

    @Test
    fun `unknown mime returns null — caller can fall back`() {
        assertNull(ExportFormatNegotiator.fromMime("application/octet-stream"))
        assertNull(ExportFormatNegotiator.fromMime("image/png"))
    }

    @Test
    fun `unknown extension returns null`() {
        assertNull(ExportFormatNegotiator.fromExtension("doc.docx"))
        assertNull(ExportFormatNegotiator.fromExtension("doc.xml"))
    }

    @Test
    fun `null and blank inputs return null cleanly`() {
        assertNull(ExportFormatNegotiator.fromMime(null))
        assertNull(ExportFormatNegotiator.fromMime(""))
        assertNull(ExportFormatNegotiator.fromMime("   "))
        assertNull(ExportFormatNegotiator.fromExtension(null))
        assertNull(ExportFormatNegotiator.fromExtension(""))
        assertNull(ExportFormatNegotiator.fromExtension("   "))
    }

    @Test
    fun `extension lookup handles filename without dot`() {
        // A user-supplied name with no extension can't be inferred.
        assertNull(ExportFormatNegotiator.fromExtension("plainfilename"))
    }

    @Test
    fun `extension lookup handles multiple dots — uses last segment`() {
        // "summary.v2.md" must still resolve to Markdown via the
        // trailing `.md` segment.
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromExtension("summary.v2.md"))
        assertEquals(ExportFormat.Pdf, ExportFormatNegotiator.fromExtension("archive.tar.pdf"))
    }

    @Test
    fun `hint lookup tries mime first then extension`() {
        // A combined hint (e.g. a raw "text/markdown" or a filename) is
        // a common UI parameter; the negotiator routes it through both
        // probes.
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromHint("text/markdown"))
        assertEquals(ExportFormat.Markdown, ExportFormatNegotiator.fromHint("notes.md"))
        assertEquals(ExportFormat.Pdf, ExportFormatNegotiator.fromHint("APPLICATION/PDF"))
        assertNull(ExportFormatNegotiator.fromHint("image/jpeg"))
        assertNull(ExportFormatNegotiator.fromHint(""))
    }
}
