package com.po4yka.ratatoskr.util.screenshot

/**
 * Pure deterministic golden-PNG path builder for the upcoming
 * Roborazzi snapshot suite. Companion to [ScreenshotDiffPolicy] — that
 * atom decides pass/fail; this atom decides where the golden lives so
 * every host (CI on Linux, dev on Mac) constructs the same path.
 *
 * Layout pinned:
 * `<section>/<variant>__<theme>__<density>.png`
 *
 * Each segment must be kebab-case ASCII (`[a-z0-9]+(-[a-z0-9]+)*`) so
 * the path stays portable. Mixed case, underscores inside a segment,
 * blank segments, and slashes inside a segment all return `null` so
 * the test runner fails loud rather than silently writing to a
 * non-portable location.
 *
 * Pure, side-effect-free, deterministic.
 */
object ScreenshotGoldenPath {
    const val SEGMENT_SEPARATOR: String = "__"
    private const val SECTION_SEPARATOR = "/"
    private const val EXTENSION = ".png"
    private val KEBAB_REGEX = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")

    fun build(
        sectionId: String,
        variantId: String,
        themeId: String,
        densityId: String,
    ): String? {
        val segments = listOf(sectionId, variantId, themeId, densityId)
        if (segments.any { !KEBAB_REGEX.matches(it) }) return null
        return sectionId + SECTION_SEPARATOR + variantId + SEGMENT_SEPARATOR + themeId +
            SEGMENT_SEPARATOR + densityId + EXTENSION
    }
}
