package com.po4yka.ratatoskr.util.share

/**
 * Canonical label formatter for the clipboard-suggestion banner that lives
 * above `SummaryListScreen`. Bridges the two-host asymmetry already pinned
 * by `ClipboardSuggestionResolver`:
 *  - Android (Show with known URL) → `Submit <truncated-host-and-path>`
 *  - iOS (Show with unknown URL, only `hasUrl()` succeeded) →
 *    `Submit URL from clipboard`
 *
 * The label is meant to sit inside a Frost `BracketButton` so the budget is
 * tight — default [DEFAULT_MAX_LABEL_LENGTH] of 40 keeps the bracket from
 * wrapping on a phone-width screen.
 *
 * Pure, side-effect-free, deterministic.
 */
object ClipboardBannerLabel {
    const val FALLBACK_LABEL: String = "Submit URL from clipboard"
    const val DEFAULT_MAX_LABEL_LENGTH: Int = 40
    private const val MINIMUM_LEGIBLE_LENGTH: Int = "Submit X…".length
    private const val PREFIX: String = "Submit "
    private const val ELLIPSIS: String = "…"
    private val SCHEME_REGEX = Regex("^https?://", RegexOption.IGNORE_CASE)

    fun format(
        urlIfKnown: String?,
        maxLabelLength: Int = DEFAULT_MAX_LABEL_LENGTH,
    ): String {
        val trimmed = urlIfKnown?.trim().orEmpty()
        if (trimmed.isEmpty()) return FALLBACK_LABEL
        val compact = trimmed.replaceFirst(SCHEME_REGEX, replacement = "").trimEnd('/')
        if (compact.isEmpty()) return FALLBACK_LABEL
        val budget = if (maxLabelLength < MINIMUM_LEGIBLE_LENGTH) MINIMUM_LEGIBLE_LENGTH else maxLabelLength
        val full = PREFIX + compact
        if (full.length <= budget) return full
        val keep = budget - PREFIX.length - ELLIPSIS.length
        return PREFIX + compact.take(keep) + ELLIPSIS
    }
}
