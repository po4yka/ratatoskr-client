package com.po4yka.ratatoskr.util.observability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DiagnosticsVisibilityTest {
    @Test
    fun `debug build always shows diagnostics — toggle off`() {
        // Debug builds expose every diagnostics surface unconditionally
        // so developers don't have to flip the switch on every fresh
        // install.
        assertTrue(
            DiagnosticsVisibility.shouldShow(
                isReleaseBuild = false,
                diagnosticsToggleEnabled = false,
            ),
        )
    }

    @Test
    fun `debug build always shows diagnostics — toggle on`() {
        assertTrue(
            DiagnosticsVisibility.shouldShow(
                isReleaseBuild = false,
                diagnosticsToggleEnabled = true,
            ),
        )
    }

    @Test
    fun `release build with toggle on shows diagnostics`() {
        // Opt-in path: the user enabled diagnostics under
        // Settings → Help.
        assertTrue(
            DiagnosticsVisibility.shouldShow(
                isReleaseBuild = true,
                diagnosticsToggleEnabled = true,
            ),
        )
    }

    @Test
    fun `release build with toggle off hides diagnostics`() {
        // Default release-build behavior — sync-health screen and other
        // diagnostics entries are hidden until the user opts in.
        assertFalse(
            DiagnosticsVisibility.shouldShow(
                isReleaseBuild = true,
                diagnosticsToggleEnabled = false,
            ),
        )
    }

    @Test
    fun `truth table is exhaustive — exactly one false-cell`() {
        // Pin the predicate's shape: the only "hide" cell is
        // (release=true, toggle=false). Any future regression that
        // hides a debug build or shows a release default is caught.
        val cells =
            listOf(
                Triple(false, false, true),
                Triple(false, true, true),
                Triple(true, false, false),
                Triple(true, true, true),
            )
        cells.forEach { (release, toggle, expected) ->
            assertEquals(
                expected,
                DiagnosticsVisibility.shouldShow(
                    isReleaseBuild = release,
                    diagnosticsToggleEnabled = toggle,
                ),
                "shouldShow(release=$release, toggle=$toggle) should be $expected",
            )
        }
    }

    @Test
    fun `decision is deterministic — same inputs map to same output`() {
        val a = DiagnosticsVisibility.shouldShow(true, false)
        val b = DiagnosticsVisibility.shouldShow(true, false)
        assertEquals(a, b)
    }
}
