package com.po4yka.ratatoskr.util.screenshot

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScreenshotGoldenPathTest {
    @Test
    fun `canonical inputs — section variant theme density joined deterministically`() {
        // Pin the canonical golden-PNG layout: a path the Roborazzi
        // runner can construct the same way on every host.
        assertEquals(
            "bracket-button/default__mono-light__phone.png",
            ScreenshotGoldenPath.build(
                sectionId = "bracket-button",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            ),
        )
    }

    @Test
    fun `dark theme variant — distinct path from light`() {
        // Each theme produces a separate golden so a theme regression
        // is localized to its own file diff rather than overwriting
        // the shared row.
        val light =
            ScreenshotGoldenPath.build(
                sectionId = "bracket-button",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            )
        val dark =
            ScreenshotGoldenPath.build(
                sectionId = "bracket-button",
                variantId = "default",
                themeId = "mono-dark",
                densityId = "phone",
            )
        assertEquals(
            "bracket-button/default__mono-light__phone.png",
            light,
        )
        assertEquals(
            "bracket-button/default__mono-dark__phone.png",
            dark,
        )
    }

    @Test
    fun `non-kebab-case section — null, build refuses`() {
        // The path must stay stable across hosts. Mixed case or
        // underscores in a segment would silently produce a
        // non-portable path; refuse.
        assertNull(
            ScreenshotGoldenPath.build(
                sectionId = "BracketButton",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            ),
        )
        assertNull(
            ScreenshotGoldenPath.build(
                sectionId = "bracket_button",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            ),
        )
    }

    @Test
    fun `blank segment — null`() {
        assertNull(
            ScreenshotGoldenPath.build(
                sectionId = "  ",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            ),
        )
        assertNull(
            ScreenshotGoldenPath.build(
                sectionId = "bracket-button",
                variantId = "",
                themeId = "mono-light",
                densityId = "phone",
            ),
        )
    }

    @Test
    fun `slash in any segment — null, defensive against path traversal`() {
        // The atom is reused across hosts (CI on Linux, dev on Mac).
        // Refusing slashes inside segments removes a class of
        // accidental directory-traversal bugs from the runner.
        assertNull(
            ScreenshotGoldenPath.build(
                sectionId = "../escape",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            ),
        )
    }

    @Test
    fun `segments are joined with single section slash and double underscore separator`() {
        // Pin the format constants so a future runner reading the
        // path can reverse-parse without reinventing the separator.
        val path =
            ScreenshotGoldenPath.build(
                sectionId = "status-badge",
                variantId = "error",
                themeId = "sepia",
                densityId = "tablet",
            )
        assertEquals("status-badge/error__sepia__tablet.png", path)
    }

    @Test
    fun `build is deterministic`() {
        val a =
            ScreenshotGoldenPath.build(
                sectionId = "bracket-button",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            )
        val b =
            ScreenshotGoldenPath.build(
                sectionId = "bracket-button",
                variantId = "default",
                themeId = "mono-light",
                densityId = "phone",
            )
        assertEquals(a, b)
    }
}
