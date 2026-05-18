package com.po4yka.ratatoskr.util.codebase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiComponentScanArgumentTest {
    @Test
    fun `feature root — Valid`() {
        assertEquals(
            DiComponentScanArgument.Outcome.Valid,
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature.auth",
                featureName = "auth",
            ),
        )
    }

    @Test
    fun `feature subpath domain usecase — Valid`() {
        // Pin the migration target shape — the post-migration use-case
        // scanner anchors here.
        assertEquals(
            DiComponentScanArgument.Outcome.Valid,
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature.summary.domain.usecase",
                featureName = "summary",
            ),
        )
    }

    @Test
    fun `legacy shared domain usecase — NotFeatureScoped`() {
        // The exact pre-migration state in CoreCommonModule.kt:6-7:
        // @ComponentScan("com.po4yka.ratatoskr.domain.usecase"). Pin
        // so the audit can surface the legacy ComponentScan as well
        // as the legacy package declaration.
        assertEquals(
            DiComponentScanArgument.Outcome.NotFeatureScoped,
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.domain.usecase",
                featureName = "summary",
            ),
        )
    }

    @Test
    fun `wrong feature name in subpath — WrongFeatureName`() {
        // A scanner that says "auth" but anchors at .feature.summary
        // would resolve the wrong module. Distinct outcome so the
        // audit explains the typo precisely.
        val outcome =
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature.summary.domain.usecase",
                featureName = "auth",
            )
        assertTrue(outcome is DiComponentScanArgument.Outcome.WrongFeatureName)
        val wrong = outcome as DiComponentScanArgument.Outcome.WrongFeatureName
        assertEquals("auth", wrong.expected)
        assertEquals("summary", wrong.actual)
    }

    @Test
    fun `feature prefix only without name — NotFeatureScoped`() {
        // `com.po4yka.ratatoskr.feature` alone is not a valid scanner
        // target — the feature segment must be present.
        assertEquals(
            DiComponentScanArgument.Outcome.NotFeatureScoped,
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature",
                featureName = "auth",
            ),
        )
    }

    @Test
    fun `blank arg — Blank`() {
        assertEquals(
            DiComponentScanArgument.Outcome.Blank,
            DiComponentScanArgument.validate(
                annotationArg = "   ",
                featureName = "auth",
            ),
        )
    }

    @Test
    fun `blank feature name — MissingFeatureName`() {
        // The expected name has to come from somewhere; if not
        // provided, the rule says so explicitly.
        assertEquals(
            DiComponentScanArgument.Outcome.MissingFeatureName,
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature.auth",
                featureName = "",
            ),
        )
    }

    @Test
    fun `surrounding whitespace tolerated`() {
        assertEquals(
            DiComponentScanArgument.Outcome.Valid,
            DiComponentScanArgument.validate(
                annotationArg = "  com.po4yka.ratatoskr.feature.auth.domain.usecase  ",
                featureName = "  auth  ",
            ),
        )
    }

    @Test
    fun `unrelated package — NotFeatureScoped`() {
        // A third-party shim or stray ComponentScan — not our concern.
        assertEquals(
            DiComponentScanArgument.Outcome.NotFeatureScoped,
            DiComponentScanArgument.validate(
                annotationArg = "com.example.somewhere.else",
                featureName = "auth",
            ),
        )
    }

    @Test
    fun `validate is deterministic`() {
        val a =
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature.auth.domain.usecase",
                featureName = "auth",
            )
        val b =
            DiComponentScanArgument.validate(
                annotationArg = "com.po4yka.ratatoskr.feature.auth.domain.usecase",
                featureName = "auth",
            )
        assertEquals(a, b)
    }
}
