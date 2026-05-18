package com.po4yka.ratatoskr.util.logging

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LogLevelFilterTest {
    @Test
    fun `Info threshold — Info passes`() {
        // Pin the inclusive comparison: at threshold == event, emit.
        // This is what slf4j and android.util.Log do.
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Info, threshold = LogLevel.Info))
    }

    @Test
    fun `Info threshold — Debug suppressed`() {
        // Debug is below Info; the standard release-build cutoff.
        assertFalse(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Debug, threshold = LogLevel.Info))
    }

    @Test
    fun `Info threshold — Trace suppressed`() {
        assertFalse(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Trace, threshold = LogLevel.Info))
    }

    @Test
    fun `Info threshold — Warn and Error pass`() {
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Warn, threshold = LogLevel.Info))
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Error, threshold = LogLevel.Info))
    }

    @Test
    fun `Error threshold — Warn suppressed`() {
        // Quiet-mode setup: only errors leak through.
        assertFalse(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Warn, threshold = LogLevel.Error))
    }

    @Test
    fun `Error threshold — Error passes`() {
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Error, threshold = LogLevel.Error))
    }

    @Test
    fun `Off threshold — nothing emits, not even Error`() {
        // Match slf4j Level.OFF semantics: a fully-disabled logger
        // emits nothing, including ERROR. The release pipeline relies
        // on this to silence a noisy module without removing call sites.
        LogLevel.entries.forEach { event ->
            assertFalse(
                LogLevelFilter.shouldEmit(messageLevel = event, threshold = LogLevel.Off),
                "Off threshold should suppress $event",
            )
        }
    }

    @Test
    fun `Trace threshold — everything passes except Off (no Off message)`() {
        // The verbose-mode setup. Off itself is never a message level —
        // it is threshold-only — but the predicate should not crash
        // even if a caller hands Off as the message level.
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Trace, threshold = LogLevel.Trace))
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Debug, threshold = LogLevel.Trace))
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Info, threshold = LogLevel.Trace))
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Warn, threshold = LogLevel.Trace))
        assertTrue(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Error, threshold = LogLevel.Trace))
    }

    @Test
    fun `Off as message level — never emits`() {
        // Off is a threshold-only level. If a caller passes it as the
        // message level (defensive), treat as no-op.
        assertFalse(LogLevelFilter.shouldEmit(messageLevel = LogLevel.Off, threshold = LogLevel.Trace))
    }

    @Test
    fun `shouldEmit is deterministic`() {
        val a = LogLevelFilter.shouldEmit(messageLevel = LogLevel.Info, threshold = LogLevel.Warn)
        val b = LogLevelFilter.shouldEmit(messageLevel = LogLevel.Info, threshold = LogLevel.Warn)
        assertTrue(a == b)
    }
}
