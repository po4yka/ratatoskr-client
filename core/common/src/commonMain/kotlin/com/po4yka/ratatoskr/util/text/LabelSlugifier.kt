package com.po4yka.ratatoskr.util.text

/**
 * Slugifies a human label into a Unicode-aware kebab-case identifier.
 * Used by:
 *  - export pipeline filename composition (in combination with
 *    [com.po4yka.ratatoskr.util.share.SafeFilename] for OS rules)
 *  - deep-link route construction (collection-by-slug, digest-by-slug)
 *  - search-tag normalization
 *
 * Rules:
 *  - The input is lowercased.
 *  - Each character that is not [Char.isLetterOrDigit] is replaced by
 *    `-`; runs of `-` collapse to one and leading/trailing `-` are
 *    stripped.
 *  - Letters from any script are preserved (Cyrillic, CJK, ...);
 *    transliteration is intentionally not performed.
 *  - Emojis and symbols are not letters, so they act as word breaks.
 *  - Null / empty / fully-stripped input collapses to the caller's
 *    [fallback] string — never returns the empty slug.
 *
 * Pure, side-effect-free, idempotent.
 */
object LabelSlugifier {
    fun slugify(
        raw: String?,
        fallback: String = "untitled",
    ): String {
        val mapped =
            (raw ?: "")
                .lowercase()
                .map { if (it.isLetterOrDigit()) it else '-' }
                .joinToString("")
        val collapsed = mapped.split('-').filter { it.isNotEmpty() }.joinToString("-")
        return collapsed.ifEmpty { fallback }
    }
}
