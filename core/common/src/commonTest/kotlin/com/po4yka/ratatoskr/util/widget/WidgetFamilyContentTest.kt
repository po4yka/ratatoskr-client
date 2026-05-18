package com.po4yka.ratatoskr.util.widget

import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetFamilyContentTest {
    private val snapshot =
        WidgetSnapshot(unreadCount = 5, freshestTitle = "Kotlin 2.0 release notes")

    @Test
    fun `AccessoryCircular — unread count, numeric`() {
        // Lock Screen circular complication has room for a number only —
        // ignore the freshest title entirely.
        val content = WidgetFamilyContent.render(family = WidgetFamily.AccessoryCircular, snapshot = snapshot)
        assertEquals(WidgetContent.Count(text = "5"), content)
    }

    @Test
    fun `AccessoryCircular — zero unread, placeholder`() {
        // No unread items reads better as a placeholder than "0" inside
        // the complication ring.
        val content =
            WidgetFamilyContent.render(
                family = WidgetFamily.AccessoryCircular,
                snapshot = WidgetSnapshot(unreadCount = 0, freshestTitle = "anything"),
            )
        assertEquals(WidgetContent.Empty(placeholder = WidgetFamilyContent.PLACEHOLDER), content)
    }

    @Test
    fun `AccessoryCircular — overflow capped at 99 plus`() {
        // The 2-character budget overflows fast; pin the cap so the
        // ring never tries to render "100" and wraps.
        val content =
            WidgetFamilyContent.render(
                family = WidgetFamily.AccessoryCircular,
                snapshot = WidgetSnapshot(unreadCount = 100, freshestTitle = null),
            )
        assertEquals(WidgetContent.Count(text = "99+"), content)
    }

    @Test
    fun `AccessoryCircular — negative count treated as zero`() {
        // Defensive: a clock skew or sync race could surface a negative
        // count. Show the placeholder instead of crashing the timeline.
        val content =
            WidgetFamilyContent.render(
                family = WidgetFamily.AccessoryCircular,
                snapshot = WidgetSnapshot(unreadCount = -3, freshestTitle = null),
            )
        assertEquals(WidgetContent.Empty(placeholder = WidgetFamilyContent.PLACEHOLDER), content)
    }

    @Test
    fun `AccessoryInline — single-line headline`() {
        // Inline complication fits along a single text run beside the
        // clock — single line, no wrap.
        val content = WidgetFamilyContent.render(family = WidgetFamily.AccessoryInline, snapshot = snapshot)
        assertEquals(
            WidgetContent.Headline(text = "Kotlin 2.0 release notes", maxLines = 1),
            content,
        )
    }

    @Test
    fun `AccessoryRectangular — headline up to 2 lines`() {
        val content = WidgetFamilyContent.render(family = WidgetFamily.AccessoryRectangular, snapshot = snapshot)
        assertEquals(
            WidgetContent.Headline(text = "Kotlin 2.0 release notes", maxLines = 2),
            content,
        )
    }

    @Test
    fun `StandBy — headline up to 3 lines, large body`() {
        // StandBy is bedside viewing distance; reserve more line budget
        // so the headline can wrap into the larger type size.
        val content = WidgetFamilyContent.render(family = WidgetFamily.StandBy, snapshot = snapshot)
        assertEquals(
            WidgetContent.Headline(text = "Kotlin 2.0 release notes", maxLines = 3),
            content,
        )
    }

    @Test
    fun `SystemMedium — headline up to 4 lines`() {
        // The existing home-screen widget; biggest line budget.
        val content = WidgetFamilyContent.render(family = WidgetFamily.SystemMedium, snapshot = snapshot)
        assertEquals(
            WidgetContent.Headline(text = "Kotlin 2.0 release notes", maxLines = 4),
            content,
        )
    }

    @Test
    fun `null freshest title — placeholder headline`() {
        // No fresh content yet (first install, empty library). The
        // headline families fall back to the same placeholder ring uses.
        val empty = WidgetSnapshot(unreadCount = 0, freshestTitle = null)
        assertEquals(
            WidgetContent.Headline(text = WidgetFamilyContent.PLACEHOLDER, maxLines = 2),
            WidgetFamilyContent.render(family = WidgetFamily.AccessoryRectangular, snapshot = empty),
        )
    }

    @Test
    fun `blank freshest title — treated as null`() {
        val blank = WidgetSnapshot(unreadCount = 0, freshestTitle = "   ")
        assertEquals(
            WidgetContent.Headline(text = WidgetFamilyContent.PLACEHOLDER, maxLines = 1),
            WidgetFamilyContent.render(family = WidgetFamily.AccessoryInline, snapshot = blank),
        )
    }

    @Test
    fun `render is deterministic`() {
        val a = WidgetFamilyContent.render(family = WidgetFamily.AccessoryCircular, snapshot = snapshot)
        val b = WidgetFamilyContent.render(family = WidgetFamily.AccessoryCircular, snapshot = snapshot)
        assertEquals(a, b)
    }
}
