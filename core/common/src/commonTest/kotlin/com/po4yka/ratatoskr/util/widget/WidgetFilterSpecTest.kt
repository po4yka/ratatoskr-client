package com.po4yka.ratatoskr.util.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WidgetFilterSpecTest {
    @Test
    fun `All round-trips through encode and decode`() {
        // The default state when nothing is configured yet. Encoded as a
        // map with the FILTER_KIND_KEY pinned to ALL, no payload.
        val original: WidgetFilter = WidgetFilter.All
        val encoded = WidgetFilterCodec.encode(original)
        assertEquals("ALL", encoded[WidgetFilterCodec.FILTER_KIND_KEY])
        assertNull(encoded[WidgetFilterCodec.PAYLOAD_KEY])
        assertEquals(original, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `Tag round-trips with the tag name in PAYLOAD_KEY`() {
        val original: WidgetFilter = WidgetFilter.Tag(tagName = "kotlin")
        val encoded = WidgetFilterCodec.encode(original)
        assertEquals("TAG", encoded[WidgetFilterCodec.FILTER_KIND_KEY])
        assertEquals("kotlin", encoded[WidgetFilterCodec.PAYLOAD_KEY])
        assertEquals(original, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `Collection round-trips with the collection id in PAYLOAD_KEY`() {
        val original: WidgetFilter = WidgetFilter.Collection(collectionId = "reading-queue")
        val encoded = WidgetFilterCodec.encode(original)
        assertEquals("COLLECTION", encoded[WidgetFilterCodec.FILTER_KIND_KEY])
        assertEquals("reading-queue", encoded[WidgetFilterCodec.PAYLOAD_KEY])
        assertEquals(original, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `empty map — All — first-install default`() {
        // The very first widget render after install hits an empty
        // GlanceState. The decode must default to All instead of null
        // so the widget always has something to render.
        assertEquals(WidgetFilter.All, WidgetFilterCodec.decode(emptyMap()))
    }

    @Test
    fun `unknown filter kind — All — forward-compat`() {
        // A future widget release may add a third filter kind. An older
        // build that reads the persisted state must fall back gracefully
        // to All rather than rendering an empty list.
        val encoded = mapOf(WidgetFilterCodec.FILTER_KIND_KEY to "FUTURE_KIND")
        assertEquals(WidgetFilter.All, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `Tag with missing payload — All — defensive`() {
        // Partial bundle after a write-crash: kind present, payload
        // missing. Coerce to All instead of constructing a Tag with an
        // empty name (which would silently hide every summary).
        val encoded = mapOf(WidgetFilterCodec.FILTER_KIND_KEY to "TAG")
        assertEquals(WidgetFilter.All, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `Tag with blank payload — All — defensive`() {
        val encoded =
            mapOf(
                WidgetFilterCodec.FILTER_KIND_KEY to "TAG",
                WidgetFilterCodec.PAYLOAD_KEY to "   ",
            )
        assertEquals(WidgetFilter.All, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `Collection with blank payload — All — defensive`() {
        val encoded =
            mapOf(
                WidgetFilterCodec.FILTER_KIND_KEY to "COLLECTION",
                WidgetFilterCodec.PAYLOAD_KEY to "",
            )
        assertEquals(WidgetFilter.All, WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `payload is trimmed on decode`() {
        // Glance ActionParameters can produce stray whitespace if the
        // config UI accidentally stores it. Trim on decode so the
        // SummaryRepository query never sees a leading-space tag name.
        val encoded =
            mapOf(
                WidgetFilterCodec.FILTER_KIND_KEY to "TAG",
                WidgetFilterCodec.PAYLOAD_KEY to "  kotlin  ",
            )
        assertEquals(WidgetFilter.Tag("kotlin"), WidgetFilterCodec.decode(encoded))
    }

    @Test
    fun `keys are namespaced — Glance ActionParameters must not collide`() {
        // Pin the wire-format constants. The Glance row-action codec
        // already namespaces under ratatoskr.widget.*; the filter codec
        // uses the same namespace prefix so both sets of keys can
        // coexist on the same Bundle without collision.
        assertEquals("ratatoskr.widget.filter_kind", WidgetFilterCodec.FILTER_KIND_KEY)
        assertEquals("ratatoskr.widget.filter_payload", WidgetFilterCodec.PAYLOAD_KEY)
    }
}
