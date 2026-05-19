package com.po4yka.ratatoskr.util.lint

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComposeTextAllowlistTest {
    @Test
    fun `Frost Lab demo screen — allowlisted`() {
        // FrostLab*Screen.kt under shared/sharedUI/.../ui/frost/ holds
        // intentional literal text — the lab is a component browser, not
        // a localized product surface.
        assertTrue(
            ComposeTextAllowlist.isAllowlisted(
                filePath = "shared/sharedUI/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/frost/FrostLabAtomsScreen.kt",
            ),
        )
        assertTrue(
            ComposeTextAllowlist.isAllowlisted(
                filePath =
                    "shared/sharedUI/src/commonMain/kotlin/com/po4yka/ratatoskr/" +
                        "ui/frost/FrostLabComponentBrowserScreen.kt",
            ),
        )
    }

    @Test
    fun `non-FrostLab screen under ui frost — not allowlisted`() {
        // Only the Lab demo screens are exempt. A regular reading-surface
        // composable that happens to live under ui/frost/ stays under
        // the rule.
        assertFalse(
            ComposeTextAllowlist.isAllowlisted(
                filePath = "shared/sharedUI/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/frost/ReadingThemeBottomSheet.kt",
            ),
        )
    }

    @Test
    fun `commonTest path — allowlisted`() {
        // Tests routinely build literals like Text("Hello") in fixture
        // helpers. The detekt rule must not flag them.
        assertTrue(
            ComposeTextAllowlist.isAllowlisted(
                filePath =
                    "feature/summary/src/commonTest/kotlin/com/po4yka/ratatoskr/" +
                        "feature/summary/SummaryFixtureScreenTest.kt",
            ),
        )
    }

    @Test
    fun `androidTest path — allowlisted`() {
        assertTrue(
            ComposeTextAllowlist.isAllowlisted(
                filePath = "androidApp/src/androidTest/kotlin/com/po4yka/ratatoskr/AppHostTest.kt",
            ),
        )
    }

    @Test
    fun `Test suffix file outside test source set — allowlisted`() {
        // Stray *Test.kt files (e.g., a shared fixture under main) still
        // match the test-shape predicate.
        assertTrue(
            ComposeTextAllowlist.isAllowlisted(
                filePath = "feature/summary/src/commonMain/kotlin/com/po4yka/ratatoskr/SummaryRowFixtureTest.kt",
            ),
        )
    }

    @Test
    fun `regular feature screen — not allowlisted`() {
        // Pin the negative: a real product screen with literal text
        // must flag. This is the heart of the rule.
        assertFalse(
            ComposeTextAllowlist.isAllowlisted(
                filePath = "feature/auth/src/commonMain/kotlin/com/po4yka/ratatoskr/feature/auth/ui/AuthScreen.kt",
            ),
        )
    }

    @Test
    fun `empty path — not allowlisted, defensive`() {
        assertFalse(ComposeTextAllowlist.isAllowlisted(filePath = ""))
    }

    @Test
    fun `windows-style path separators — still detected`() {
        // detekt on Windows reports backslash-separated paths. The
        // predicate must normalize so a CI run on Windows agrees with
        // the local Mac/Linux behavior.
        assertTrue(
            ComposeTextAllowlist.isAllowlisted(
                filePath =
                    "shared\\sharedUI\\src\\commonMain\\kotlin\\com\\po4yka\\ratatoskr\\ui\\frost\\FrostLabAtomsScreen.kt",
            ),
        )
    }

    @Test
    fun `predicate is deterministic`() {
        val path = "feature/auth/src/commonMain/kotlin/AuthScreen.kt"
        val a = ComposeTextAllowlist.isAllowlisted(filePath = path)
        val b = ComposeTextAllowlist.isAllowlisted(filePath = path)
        assertTrue(a == b)
    }
}
