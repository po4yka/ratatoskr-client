package com.po4yka.ratatoskr.util.a11y

/**
 * Pure builder for the screen-reader announce string that should be
 * attached to a Frost heading via Compose `semantics { contentDescription
 * = HeadingAnnounceLabel.announce(...) }`.
 *
 * Centralizes the format so every screen produces consistent TalkBack /
 * VoiceOver output rather than ad-hoc "title, heading" / "Heading: title"
 * variations that already exist on a few legacy surfaces (see
 * audit-screen-semantics-for-talkback-and-voiceover).
 *
 * Output shape:
 *  - Level 1   → `Page heading: <text>` — pronounced as the page anchor
 *                so users hear the screen's top before sublevel content.
 *  - Level 2-6 → `Heading level <N>, <text>` — matches the announce
 *                grammar Android TalkBack and iOS VoiceOver use natively
 *                for HTML headings.
 *  - Empty/blank text → `""` — caller should skip setting the semantics
 *                property rather than emitting a phantom focus stop.
 *
 * Defensive: levels outside `1..6` are clamped (HTML heading semantics
 * top out at h6; below 1 collapses to the page-heading prefix). Trailing
 * whitespace in `text` is trimmed so the reader doesn't pause at the
 * end of the announcement.
 *
 * Pure, side-effect-free, deterministic.
 */
object HeadingAnnounceLabel {
    fun announce(
        text: String,
        level: Int,
    ): String {
        val cleaned = text.trim()
        if (cleaned.isEmpty()) return ""
        val safeLevel = level.coerceIn(1, 6)
        return if (safeLevel == 1) {
            "Page heading: $cleaned"
        } else {
            "Heading level $safeLevel, $cleaned"
        }
    }
}
