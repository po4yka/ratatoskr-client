package com.po4yka.ratatoskr.util.export

/**
 * Supported summary-export formats. Each value carries its own canonical
 * MIME type and extension for round-tripping through Android's
 * `Intent.EXTRA_MIME_TYPES` and iOS's `UTType` identifiers.
 */
enum class ExportFormat(
    val canonicalMime: String,
    val canonicalExtension: String,
) {
    Markdown(canonicalMime = "text/markdown", canonicalExtension = "md"),
    Html(canonicalMime = "text/html", canonicalExtension = "html"),
    Pdf(canonicalMime = "application/pdf", canonicalExtension = "pdf"),
    PlainText(canonicalMime = "text/plain", canonicalExtension = "txt"),
}

/**
 * Pure MIME/extension → [ExportFormat] negotiator for the summary export
 * overflow menu.
 *
 * The atom is split into three probes so callers can drive the right one:
 *  - [fromMime] for intent extras / share-sheet hints.
 *  - [fromExtension] for filenames pasted by the user or returned from
 *    an Android SAF picker.
 *  - [fromHint] when the caller has a single string and doesn't know
 *    which probe applies (e.g. a deep-link query parameter); tries the
 *    MIME probe first, then the extension probe.
 *
 * All probes are case-insensitive, null-/blank-safe, and tolerant of
 * MIME parameters (`text/markdown; charset=utf-8` resolves to
 * [ExportFormat.Markdown]). An unknown value returns `null` so callers
 * can fall back to a default (typically [ExportFormat.Markdown]).
 *
 * Pure, side-effect-free, deterministic.
 */
object ExportFormatNegotiator {
    fun fromMime(mime: String?): ExportFormat? {
        if (mime.isNullOrBlank()) return null
        val normalized = mime.substringBefore(';').trim().lowercase()
        return MIME_LOOKUP[normalized]
    }

    fun fromExtension(filenameOrExtension: String?): ExportFormat? {
        if (filenameOrExtension.isNullOrBlank()) return null
        val trimmed = filenameOrExtension.trim()
        val dotIndex = trimmed.lastIndexOf('.')
        if (dotIndex < 0 || dotIndex == trimmed.lastIndex) return null
        val ext = trimmed.substring(dotIndex + 1).lowercase()
        return EXTENSION_LOOKUP[ext]
    }

    fun fromHint(value: String?): ExportFormat? = fromMime(value) ?: fromExtension(value)

    private val MIME_LOOKUP: Map<String, ExportFormat> =
        mapOf(
            "text/markdown" to ExportFormat.Markdown,
            "text/x-markdown" to ExportFormat.Markdown,
            "text/html" to ExportFormat.Html,
            "application/pdf" to ExportFormat.Pdf,
            "text/plain" to ExportFormat.PlainText,
        )

    private val EXTENSION_LOOKUP: Map<String, ExportFormat> =
        mapOf(
            "md" to ExportFormat.Markdown,
            "markdown" to ExportFormat.Markdown,
            "html" to ExportFormat.Html,
            "htm" to ExportFormat.Html,
            "pdf" to ExportFormat.Pdf,
            "txt" to ExportFormat.PlainText,
        )
}
