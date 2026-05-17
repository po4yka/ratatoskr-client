package com.po4yka.ratatoskr.util.widget

import com.po4yka.ratatoskr.util.widget.WidgetRowAction.Archive
import com.po4yka.ratatoskr.util.widget.WidgetRowAction.MarkRead
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WidgetRowActionTest {
    @Test
    fun `encode round-trips MarkRead with the summary id intact`() {
        val original = MarkRead(summaryId = "sum-123")
        val encoded = WidgetRowActionCodec.encode(original)

        // Encoded form pins the keys so the Glance ActionCallback can build
        // ActionParameters with the same names. The contract is documented as
        // string-keyed pairs so neither the Android nor the iOS widget surface
        // needs Glance ActionParameters types in commonMain.
        assertEquals("MARK_READ", encoded[WidgetRowActionCodec.ACTION_KEY])
        assertEquals("sum-123", encoded[WidgetRowActionCodec.SUMMARY_ID_KEY])

        val decoded = WidgetRowActionCodec.decode(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `encode round-trips Archive with the summary id intact`() {
        val original = Archive(summaryId = "sum-456")
        val decoded = WidgetRowActionCodec.decode(WidgetRowActionCodec.encode(original))
        assertEquals(original, decoded)
    }

    @Test
    fun `decode returns null when the action key is missing — broken intent extras`() {
        // Defends the action callback from a corrupted intent payload — Android
        // can hand us partial bundles after process death.
        val decoded =
            WidgetRowActionCodec.decode(mapOf(WidgetRowActionCodec.SUMMARY_ID_KEY to "sum-7"))
        assertNull(decoded)
    }

    @Test
    fun `decode returns null when the summary id is missing`() {
        // An action with no target summary cannot route anywhere — bail.
        val decoded =
            WidgetRowActionCodec.decode(mapOf(WidgetRowActionCodec.ACTION_KEY to "MARK_READ"))
        assertNull(decoded)
    }

    @Test
    fun `decode returns null when the summary id is blank`() {
        // Empty/whitespace-only id is treated the same as missing id; the
        // SQLDelight applier can't run a WHERE clause against an empty key.
        val decoded =
            WidgetRowActionCodec.decode(
                mapOf(
                    WidgetRowActionCodec.ACTION_KEY to "MARK_READ",
                    WidgetRowActionCodec.SUMMARY_ID_KEY to "   ",
                ),
            )
        assertNull(decoded)
    }

    @Test
    fun `decode returns null for unknown action types — forward-compatibility`() {
        // An older client receiving a future "PIN" action type must fall
        // through to null rather than guess a routing. The current callback
        // surface just logs and re-renders the widget.
        val decoded =
            WidgetRowActionCodec.decode(
                mapOf(
                    WidgetRowActionCodec.ACTION_KEY to "PIN",
                    WidgetRowActionCodec.SUMMARY_ID_KEY to "sum-1",
                ),
            )
        assertNull(decoded)
    }

    @Test
    fun `action wire format is uppercase to match Glance ActionParameters string conventions`() {
        // Pins the wire format. Decoders on both platforms expect uppercase.
        // A refactor that switches to PascalCase or lowercase fails this test.
        assertEquals("MARK_READ", WidgetRowActionCodec.encode(MarkRead("a"))[WidgetRowActionCodec.ACTION_KEY])
        assertEquals("ARCHIVE", WidgetRowActionCodec.encode(Archive("b"))[WidgetRowActionCodec.ACTION_KEY])
    }

    @Test
    fun `summaryId getter exposes the routing target uniformly across variants`() {
        // The widget callback reads action.summaryId without knowing the
        // concrete variant — defended by the sealed interface contract.
        val read: WidgetRowAction = MarkRead("a")
        val archive: WidgetRowAction = Archive("b")
        assertEquals("a", read.summaryId)
        assertEquals("b", archive.summaryId)
    }

    @Test
    fun `keys are stable strings — refactor guard`() {
        // The Glance manifest entries and the iOS widget intents both bind to
        // these key strings literally. Renaming the constants must require
        // an explicit test update.
        assertEquals("ratatoskr.widget.action", WidgetRowActionCodec.ACTION_KEY)
        assertEquals("ratatoskr.widget.summary_id", WidgetRowActionCodec.SUMMARY_ID_KEY)
    }
}
