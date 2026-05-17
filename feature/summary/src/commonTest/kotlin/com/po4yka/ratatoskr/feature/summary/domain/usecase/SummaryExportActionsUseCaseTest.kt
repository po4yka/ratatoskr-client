package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.feature.summary.export.SummaryMarkdownExporter
import com.po4yka.ratatoskr.util.share.ClipboardWriter
import com.po4yka.ratatoskr.util.share.ExternalUrlOpener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class SummaryExportActionsUseCaseTest {
    private class FakeClipboardWriter(var nextResult: Boolean = true) : ClipboardWriter {
        var lastText: String? = null
            private set
        var callCount: Int = 0
            private set

        override fun setText(text: String): Boolean {
            callCount++
            lastText = text
            return nextResult
        }
    }

    private class FakeExternalUrlOpener(var nextResult: Boolean = true) : ExternalUrlOpener {
        var lastUrl: String? = null
            private set
        var callCount: Int = 0
            private set

        override fun open(url: String): Boolean {
            callCount++
            lastUrl = url
            return nextResult
        }
    }

    private fun fixedSummary(title: String = "How Ratatoskr handles offline pending operations"): Summary =
        Summary(
            id = "smr_001",
            title = title,
            content = "TLDR body.",
            sourceUrl = "https://example.com/blog/ratatoskr-sync",
            imageUrl = null,
            createdAt = Instant.parse("2026-04-01T12:00:00Z"),
            isRead = false,
            tags = listOf("kmp", "sync"),
            readingTimeMin = 4,
        )

    @Test
    fun `copyAsMarkdown writes exporter output to clipboard verbatim`() {
        val clipboard = FakeClipboardWriter()
        val opener = FakeExternalUrlOpener()
        val useCase = SummaryExportActionsUseCase(clipboard, opener)
        val summary = fixedSummary()
        val expected = SummaryMarkdownExporter.toMarkdown(summary)

        val ok = useCase.copyAsMarkdown(summary)

        assertTrue(ok)
        assertEquals(1, clipboard.callCount)
        assertEquals(expected, clipboard.lastText)
        // Opening Obsidian must not be triggered by the copy action.
        assertEquals(0, opener.callCount)
    }

    @Test
    fun `copyAsMarkdown propagates a false result when the platform refuses the clipboard write`() {
        val clipboard = FakeClipboardWriter(nextResult = false)
        val useCase = SummaryExportActionsUseCase(clipboard, FakeExternalUrlOpener())

        assertFalse(useCase.copyAsMarkdown(fixedSummary()))
    }

    @Test
    fun `openInObsidian dispatches an obsidian new url whose content matches the exporter output`() {
        val clipboard = FakeClipboardWriter()
        val opener = FakeExternalUrlOpener()
        val useCase = SummaryExportActionsUseCase(clipboard, opener)
        val summary = fixedSummary()

        val ok = useCase.openInObsidian(summary, vault = "Knowledge")

        assertTrue(ok)
        assertEquals(1, opener.callCount)
        // Writing to the clipboard is not part of the deep-link flow.
        assertEquals(0, clipboard.callCount)

        val url = opener.lastUrl!!
        assertTrue(url.startsWith("obsidian://new?"), "expected obsidian new URL, got: $url")
        assertTrue(url.contains("vault=Knowledge"), "vault arg must be present: $url")
        // The title should be percent-encoded into the name arg.
        assertTrue(url.contains("name=How%20Ratatoskr%20handles%20offline%20pending%20operations"))
    }

    @Test
    fun `openInObsidian omits the vault arg when none is configured`() {
        val opener = FakeExternalUrlOpener()
        val useCase = SummaryExportActionsUseCase(FakeClipboardWriter(), opener)

        useCase.openInObsidian(fixedSummary())

        val url = opener.lastUrl!!
        assertFalse(url.contains("vault="), "blank vault must be omitted so Obsidian picks last-used: $url")
    }

    @Test
    fun `openInObsidian returns false when no app handles the obsidian scheme`() {
        val opener = FakeExternalUrlOpener(nextResult = false)
        val useCase = SummaryExportActionsUseCase(FakeClipboardWriter(), opener)

        assertFalse(useCase.openInObsidian(fixedSummary()))
    }
}
