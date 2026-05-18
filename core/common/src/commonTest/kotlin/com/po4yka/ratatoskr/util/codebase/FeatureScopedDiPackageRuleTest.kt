package com.po4yka.ratatoskr.util.codebase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureScopedDiPackageRuleTest {
    @Test
    fun `correct feature-scoped DI package — Valid`() {
        // The migration target. AuthFeatureBindings.kt should declare
        // `package com.po4yka.ratatoskr.feature.auth.di`.
        assertEquals(
            FeatureScopedDiPackageRule.Outcome.Valid,
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.feature.auth.di",
                featureName = "auth",
            ),
        )
    }

    @Test
    fun `legacy shared di package — WrongPackage`() {
        // The exact pre-migration state across feature modules: every
        // DI binding file declares `package com.po4yka.ratatoskr.di`.
        // Pin the error shape so the audit emits a useful "expected X,
        // got Y" message.
        val outcome =
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.di",
                featureName = "summary",
            )
        assertTrue(outcome is FeatureScopedDiPackageRule.Outcome.WrongPackage)
        val wrong = outcome as FeatureScopedDiPackageRule.Outcome.WrongPackage
        assertEquals("com.po4yka.ratatoskr.feature.summary.di", wrong.expected)
        assertEquals("com.po4yka.ratatoskr.di", wrong.actual)
    }

    @Test
    fun `wrong feature name in package — WrongPackage`() {
        // Defensive: a misnamed file would silently bind to the wrong
        // feature scope. The rule flags so the migration check catches
        // a typo across the cluster.
        val outcome =
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.feature.collections.di",
                featureName = "summary",
            )
        assertTrue(outcome is FeatureScopedDiPackageRule.Outcome.WrongPackage)
    }

    @Test
    fun `subpackage extension — WrongPackage — DI module sits at exactly _di_`() {
        // `com.po4yka.ratatoskr.feature.auth.di.internal` may compile
        // but the canonical convention is the leaf at exactly `.di`.
        // Reject so the KSP scan anchors at the canonical leaf.
        val outcome =
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.feature.auth.di.internal",
                featureName = "auth",
            )
        assertTrue(outcome is FeatureScopedDiPackageRule.Outcome.WrongPackage)
    }

    @Test
    fun `blank package statement — Blank`() {
        assertEquals(
            FeatureScopedDiPackageRule.Outcome.Blank,
            FeatureScopedDiPackageRule.validate(
                packageStatement = "  ",
                featureName = "auth",
            ),
        )
    }

    @Test
    fun `blank feature name — MissingFeatureName`() {
        // Defensive: a feature name is required to construct the
        // expected target string. If the caller can't supply one, the
        // rule says so explicitly rather than computing a meaningless
        // diff against `feature..di`.
        assertEquals(
            FeatureScopedDiPackageRule.Outcome.MissingFeatureName,
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.feature.auth.di",
                featureName = "   ",
            ),
        )
    }

    @Test
    fun `surrounding whitespace tolerated`() {
        assertEquals(
            FeatureScopedDiPackageRule.Outcome.Valid,
            FeatureScopedDiPackageRule.validate(
                packageStatement = "  com.po4yka.ratatoskr.feature.auth.di  ",
                featureName = "  auth  ",
            ),
        )
    }

    @Test
    fun `validate is deterministic`() {
        val a =
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.feature.auth.di",
                featureName = "auth",
            )
        val b =
            FeatureScopedDiPackageRule.validate(
                packageStatement = "com.po4yka.ratatoskr.feature.auth.di",
                featureName = "auth",
            )
        assertEquals(a, b)
    }
}
