package com.po4yka.ratatoskr.util.widget

/**
 * The iOS widget families that `RecentSummariesWidget.supportedFamilies`
 * advertises plus the existing systemMedium. Kept as a pure enum so the
 * decision atom does not pull in WidgetKit symbols.
 */
enum class WidgetFamily {
    AccessoryCircular,
    AccessoryRectangular,
    AccessoryInline,
    StandBy,
    SystemMedium,
}

/** Snapshot of timeline state for content-fit decisions. */
data class WidgetSnapshot(
    val unreadCount: Int,
    val freshestTitle: String?,
)

/**
 * Variant chosen by [WidgetFamilyContent.render] — concrete enough that
 * the Swift renderer can pattern-match without re-deriving line budgets.
 */
sealed interface WidgetContent {
    data class Count(val text: String) : WidgetContent

    data class Headline(val text: String, val maxLines: Int) : WidgetContent

    data class Empty(val placeholder: String) : WidgetContent
}

/**
 * Decides what to draw for each widget family from a single snapshot.
 * The circular complication is numeric-only; the other families carry
 * the freshest summary headline scaled to a per-family line budget.
 *
 * Pure, side-effect-free, deterministic.
 */
object WidgetFamilyContent {
    const val PLACEHOLDER: String = "—"
    private const val OVERFLOW_THRESHOLD = 100
    private const val OVERFLOW_LABEL = "99+"

    fun render(
        family: WidgetFamily,
        snapshot: WidgetSnapshot,
    ): WidgetContent =
        when (family) {
            WidgetFamily.AccessoryCircular -> renderCircular(snapshot.unreadCount)
            WidgetFamily.AccessoryInline -> headline(snapshot.freshestTitle, maxLines = 1)
            WidgetFamily.AccessoryRectangular -> headline(snapshot.freshestTitle, maxLines = 2)
            WidgetFamily.StandBy -> headline(snapshot.freshestTitle, maxLines = 3)
            WidgetFamily.SystemMedium -> headline(snapshot.freshestTitle, maxLines = 4)
        }

    private fun renderCircular(unreadCount: Int): WidgetContent =
        when {
            unreadCount <= 0 -> WidgetContent.Empty(placeholder = PLACEHOLDER)
            unreadCount >= OVERFLOW_THRESHOLD -> WidgetContent.Count(text = OVERFLOW_LABEL)
            else -> WidgetContent.Count(text = unreadCount.toString())
        }

    private fun headline(
        freshestTitle: String?,
        maxLines: Int,
    ): WidgetContent {
        val trimmed = freshestTitle?.trim().orEmpty()
        val text = trimmed.ifEmpty { PLACEHOLDER }
        return WidgetContent.Headline(text = text, maxLines = maxLines)
    }
}
