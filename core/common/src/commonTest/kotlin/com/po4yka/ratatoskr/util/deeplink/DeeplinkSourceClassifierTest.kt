package com.po4yka.ratatoskr.util.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals

class DeeplinkSourceClassifierTest {
    @Test
    fun `https with the canonical host — UniversalLink`() {
        // Universal Links / App Links arrive as https://ratatoskr.po4yka.com/...
        assertEquals(
            DeeplinkSource.UniversalLink,
            DeeplinkSourceClassifier.classify("https://ratatoskr.po4yka.com/s/abc123"),
        )
    }

    @Test
    fun `https with a different host — Unknown`() {
        // Some other share-sheet route may hand the host app an https
        // URL we don't claim — don't mis-tag it as our universal link.
        assertEquals(
            DeeplinkSource.Unknown,
            DeeplinkSourceClassifier.classify("https://example.com/s/abc123"),
        )
    }

    @Test
    fun `http with the canonical host is still UniversalLink — scheme upgrade is parser's job`() {
        // The parser already upgrades http→https; the source classifier
        // only cares that the host claims us.
        assertEquals(
            DeeplinkSource.UniversalLink,
            DeeplinkSourceClassifier.classify("http://ratatoskr.po4yka.com/s/abc123"),
        )
    }

    @Test
    fun `host match is case-insensitive`() {
        assertEquals(
            DeeplinkSource.UniversalLink,
            DeeplinkSourceClassifier.classify("https://RATATOSKR.po4yka.com/s/x"),
        )
    }

    @Test
    fun `ratatoskr scheme — CustomScheme`() {
        // The custom `ratatoskr://` scheme is reserved for the
        // PendingIntent the Android widget builds and for legacy
        // notifications. Tag distinctly so analytics can separate the
        // widget-tap signal from the share-extension signal.
        assertEquals(
            DeeplinkSource.CustomScheme,
            DeeplinkSourceClassifier.classify("ratatoskr://summary/abc123"),
        )
    }

    @Test
    fun `ratatoskr scheme match is case-insensitive`() {
        assertEquals(
            DeeplinkSource.CustomScheme,
            DeeplinkSourceClassifier.classify("RATATOSKR://summary/abc"),
        )
    }

    @Test
    fun `other custom scheme — Unknown`() {
        assertEquals(
            DeeplinkSource.Unknown,
            DeeplinkSourceClassifier.classify("myapp://summary/abc"),
        )
    }

    @Test
    fun `empty or blank — Unknown`() {
        assertEquals(DeeplinkSource.Unknown, DeeplinkSourceClassifier.classify(""))
        assertEquals(DeeplinkSource.Unknown, DeeplinkSourceClassifier.classify("   "))
    }

    @Test
    fun `null — Unknown`() {
        assertEquals(DeeplinkSource.Unknown, DeeplinkSourceClassifier.classify(null))
    }

    @Test
    fun `garbage — Unknown`() {
        assertEquals(DeeplinkSource.Unknown, DeeplinkSourceClassifier.classify("not a url"))
        assertEquals(DeeplinkSource.Unknown, DeeplinkSourceClassifier.classify("javascript:alert(1)"))
    }

    @Test
    fun `classification is deterministic`() {
        val a = DeeplinkSourceClassifier.classify("https://ratatoskr.po4yka.com/s/x")
        val b = DeeplinkSourceClassifier.classify("https://ratatoskr.po4yka.com/s/x")
        assertEquals(a, b)
    }
}
