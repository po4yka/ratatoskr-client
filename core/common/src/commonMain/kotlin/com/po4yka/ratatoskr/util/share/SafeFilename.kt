package com.po4yka.ratatoskr.util.share

/**
 * Sanitizes a user-supplied title into a safe filename for the
 * `SummaryMarkdownExporter` and the share extensions. The output is
 * the cross-platform-safe intersection of NTFS / APFS / ext4 /
 * Android scoped-storage rules:
 *
 *  - Windows-forbidden punctuation `< > : " / \ | ? *` is removed
 *    outright (no substitution — two adjacent forbidden chars collapse
 *    rather than producing noise).
 *  - ISO C0 control characters and DEL are removed.
 *  - Surrounding whitespace and dots are trimmed (Windows silently
 *    strips trailing dots on file creation).
 *  - Exact-match Windows device names (CON, PRN, AUX, NUL, COM1-9,
 *    LPT1-9) get an underscore suffix; partial-match names like
 *    "CONFIG" are preserved.
 *  - The result is capped at [MAX_LENGTH] (200 chars) — the most
 *    restrictive common filesystem limit.
 *  - Null, empty, or fully-stripped input collapses to [fallback] so
 *    the caller never has to handle a "/" trailing path.
 *
 * Pure, side-effect-free, idempotent.
 */
object SafeFilename {
    const val MAX_LENGTH = 200

    private val FORBIDDEN = setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')

    private val WINDOWS_DEVICE_NAMES =
        buildSet {
            addAll(listOf("CON", "PRN", "AUX", "NUL"))
            for (i in 1..9) {
                add("COM$i")
                add("LPT$i")
            }
        }

    fun sanitize(
        raw: String?,
        fallback: String = "untitled",
    ): String {
        val cleaned =
            (raw ?: "")
                .asSequence()
                .filterNot { it in FORBIDDEN || isControl(it) }
                .joinToString("")
                .trim()
                .trim('.')
                .take(MAX_LENGTH)

        if (cleaned.isEmpty()) return fallback
        if (cleaned.uppercase() in WINDOWS_DEVICE_NAMES) return cleaned + "_"
        return cleaned
    }

    private fun isControl(ch: Char): Boolean = ch.code < 0x20 || ch.code == 0x7F
}
