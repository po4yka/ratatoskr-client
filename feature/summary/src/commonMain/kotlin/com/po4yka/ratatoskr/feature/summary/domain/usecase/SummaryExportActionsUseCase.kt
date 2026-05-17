package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.feature.summary.export.ObsidianDeepLink
import com.po4yka.ratatoskr.feature.summary.export.SummaryMarkdownExporter
import com.po4yka.ratatoskr.util.share.ClipboardWriter
import com.po4yka.ratatoskr.util.share.ExternalUrlOpener
import org.koin.core.annotation.Factory

/**
 * Orchestrates the two SummaryDetailScreen overflow-menu actions:
 *  - **Copy as Markdown** — runs the deterministic [SummaryMarkdownExporter]
 *    and hands the output to the system clipboard via [ClipboardWriter].
 *  - **Open in Obsidian** — composes an `obsidian://new?…` URL via
 *    [ObsidianDeepLink] (vault optional — empty string lets Obsidian pick the
 *    last-used vault) and dispatches it through [ExternalUrlOpener]. Returns
 *    `false` when the OS reports no app registered for the `obsidian://`
 *    scheme, so the caller can show a "Obsidian isn't installed" toast.
 *
 * Why a use case and not direct ViewModel calls: the exporter is pure but the
 * clipboard + URL-opener calls are platform-side-effecting and hard to test in
 * a ViewModel unit test. Centralizing the orchestration here lets the
 * SummaryDetailViewModel stay a thin glue layer and gives us a single
 * commonTest seam (`FakeClipboardWriter` + `FakeExternalUrlOpener`) that
 * proves the exporter output is what hits the clipboard and the deep-link URL
 * is what gets dispatched.
 */
@Factory
class SummaryExportActionsUseCase(
    private val clipboardWriter: ClipboardWriter,
    private val externalUrlOpener: ExternalUrlOpener,
) {
    /** Returns `true` if the clipboard write succeeded. */
    fun copyAsMarkdown(summary: Summary): Boolean {
        val markdown = SummaryMarkdownExporter.toMarkdown(summary)
        return clipboardWriter.setText(markdown)
    }

    /**
     * Returns `true` if the OS dispatched the `obsidian://new?…` URL to a
     * handler. `false` means Obsidian isn't installed (or refused the URL).
     *
     * @param vault Obsidian vault name. Empty string lets Obsidian pick the
     *              last-used vault — typical default for users who haven't set
     *              a preference yet.
     */
    fun openInObsidian(
        summary: Summary,
        vault: String = "",
    ): Boolean {
        val markdown = SummaryMarkdownExporter.toMarkdown(summary)
        val url =
            ObsidianDeepLink.composeNewNote(
                vault = vault,
                name = summary.title,
                content = markdown,
            )
        return externalUrlOpener.open(url)
    }
}
