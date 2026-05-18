package com.po4yka.ratatoskr.util.export

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportFilenameComposerTest {
    @Test
    fun `simple title + Markdown — slug plus extension`() {
        assertEquals(
            "an-example-article.md",
            ExportFilenameComposer.compose(
                title = "An Example Article",
                format = ExportFormat.Markdown,
            ),
        )
    }

    @Test
    fun `each format produces its canonical extension`() {
        assertEquals(
            "title.html",
            ExportFilenameComposer.compose(title = "Title", format = ExportFormat.Html),
        )
        assertEquals(
            "title.pdf",
            ExportFilenameComposer.compose(title = "Title", format = ExportFormat.Pdf),
        )
        assertEquals(
            "title.txt",
            ExportFilenameComposer.compose(title = "Title", format = ExportFormat.PlainText),
        )
    }

    @Test
    fun `forbidden filesystem chars are stripped via the SafeFilename atom`() {
        // The composer routes through SafeFilename which removes
        // Windows-forbidden chars (/:*?"<>|) so the output is safe on
        // every desktop / mobile filesystem.
        val out =
            ExportFilenameComposer.compose(
                title = "Doc/With:Bad*Chars",
                format = ExportFormat.Markdown,
            )
        assertTrue('/' !in out)
        assertTrue(':' !in out)
        assertTrue('*' !in out)
    }

    @Test
    fun `blank title falls back to untitled-with-format-extension`() {
        // The slugifier collapses blank/empty input to its fallback;
        // the composer wraps it with the format's canonical extension.
        assertEquals(
            "untitled.md",
            ExportFilenameComposer.compose(
                title = "",
                format = ExportFormat.Markdown,
            ),
        )
        assertEquals(
            "untitled.pdf",
            ExportFilenameComposer.compose(
                title = "   ",
                format = ExportFormat.Pdf,
            ),
        )
    }

    @Test
    fun `unicode title is preserved in lowercase kebab-case slug`() {
        // LabelSlugifier keeps letters from any script; pin that the
        // Cyrillic input round-trips through the composer.
        val out =
            ExportFilenameComposer.compose(
                title = "Привет Мир",
                format = ExportFormat.Markdown,
            )
        assertEquals("привет-мир.md", out)
    }

    @Test
    fun `composition is deterministic`() {
        val a =
            ExportFilenameComposer.compose(
                title = "Sample Title",
                format = ExportFormat.Markdown,
            )
        val b =
            ExportFilenameComposer.compose(
                title = "Sample Title",
                format = ExportFormat.Markdown,
            )
        assertEquals(a, b)
    }

    @Test
    fun `output always ends with the format extension`() {
        for (format in ExportFormat.entries) {
            val out =
                ExportFilenameComposer.compose(
                    title = "Whatever Title",
                    format = format,
                )
            assertTrue(
                out.endsWith(".${format.canonicalExtension}"),
                "output '$out' missing .${format.canonicalExtension} suffix",
            )
        }
    }

    @Test
    fun `excess length is trimmed so the full name fits within filesystem limits`() {
        // Compose a very long title that would blow past SafeFilename's
        // MAX_LENGTH; the composer must produce a name that still fits.
        val title = "long title segment ".repeat(40)
        val out =
            ExportFilenameComposer.compose(
                title = title,
                format = ExportFormat.Markdown,
            )
        assertTrue(
            out.length <= 200,
            "filename '$out' length=${out.length} exceeds 200",
        )
        assertTrue(out.endsWith(".md"), "long filename '$out' lost its extension")
    }
}
