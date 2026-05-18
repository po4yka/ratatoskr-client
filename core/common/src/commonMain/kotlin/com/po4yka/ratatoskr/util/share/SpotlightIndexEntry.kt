package com.po4yka.ratatoskr.util.share

/**
 * Fields the iOS layer hands to `CSSearchableItem` /
 * `CSSearchableItemAttributeSet` when indexing a summary into Spotlight.
 * Kept as a pure data class in `commonMain` so the same atom can be
 * exercised by KMP tests without an iOS framework on the JVM classpath.
 *
 *  - [uniqueIdentifier]    — `summary:<id>`, the CSSearchableItem domain id.
 *  - [title]               — display title; falls back to
 *                            [SpotlightIndexEntry.UNTITLED_FALLBACK] for blank input.
 *  - [contentDescription]  — short snippet, capped at
 *                            [SpotlightIndexEntry.MAX_DESCRIPTION_CHARS].
 *  - [deepLinkUri]         — `ratatoskr://summary/<id>`, opened by the host
 *                            on Spotlight tap.
 */
data class SpotlightIndexEntry(
    val uniqueIdentifier: String,
    val title: String,
    val contentDescription: String,
    val deepLinkUri: String,
) {
    companion object {
        const val UNTITLED_FALLBACK: String = "Untitled summary"
        const val MAX_DESCRIPTION_CHARS: Int = 200
        private const val IDENTIFIER_PREFIX = "summary:"
        private const val DEEP_LINK_PREFIX = "ratatoskr://summary/"

        /**
         * Build a Spotlight entry for one summary. Returns `null` for an
         * unusable id (blank), so the caller can skip indexing with a
         * `?.let { CSSearchableItem(...) }` without an explicit branch.
         *
         * Pure, side-effect-free, deterministic.
         */
        fun build(
            summaryId: String,
            title: String,
            snippet: String,
        ): SpotlightIndexEntry? {
            val id = summaryId.trim()
            if (id.isEmpty()) return null
            val displayTitle = title.trim().ifEmpty { UNTITLED_FALLBACK }
            val description = snippet.trim().take(MAX_DESCRIPTION_CHARS)
            return SpotlightIndexEntry(
                uniqueIdentifier = IDENTIFIER_PREFIX + id,
                title = displayTitle,
                contentDescription = description,
                deepLinkUri = DEEP_LINK_PREFIX + id,
            )
        }
    }
}
