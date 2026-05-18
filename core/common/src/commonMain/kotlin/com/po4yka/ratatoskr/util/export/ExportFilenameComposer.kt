package com.po4yka.ratatoskr.util.export

import com.po4yka.ratatoskr.util.share.SafeFilename
import com.po4yka.ratatoskr.util.text.LabelSlugifier

/**
 * Composes a filesystem-safe filename for the summary export overflow
 * menu by routing a user-visible title through:
 *
 *  1. [LabelSlugifier] to lowercase kebab-case the title (Unicode-aware).
 *  2. [SafeFilename] to strip OS-forbidden chars (`/:*?"<>|`), control
 *     chars, Windows device-name reservations, and to bound the length.
 *  3. The format's [ExportFormat.canonicalExtension] is appended.
 *
 * Output guarantees:
 *  - Always ends with `.{ext}` for the chosen [ExportFormat].
 *  - Never contains a path separator or any other OS-forbidden char.
 *  - Length stays inside the 200-byte filename budget shared with
 *    [SafeFilename].
 *  - Empty/blank input collapses to `untitled.{ext}`.
 *
 * Pure, side-effect-free, deterministic. Composition of three existing
 * atoms; no new logic, just the canonical chaining order so each call
 * site doesn't re-derive it.
 */
object ExportFilenameComposer {
    fun compose(
        title: String,
        format: ExportFormat,
    ): String {
        val slug = LabelSlugifier.slugify(title, fallback = "untitled")
        val safe = SafeFilename.sanitize(slug, fallback = "untitled")
        val budget = MAX_FILENAME_LENGTH - format.canonicalExtension.length - 1
        val trimmed = if (safe.length > budget) safe.substring(0, budget) else safe
        return "$trimmed.${format.canonicalExtension}"
    }

    private const val MAX_FILENAME_LENGTH: Int = 200
}
