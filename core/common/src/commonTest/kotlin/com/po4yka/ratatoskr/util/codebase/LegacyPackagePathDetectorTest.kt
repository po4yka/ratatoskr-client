package com.po4yka.ratatoskr.util.codebase

import kotlin.test.Test
import kotlin.test.assertEquals

class LegacyPackagePathDetectorTest {
    @Test
    fun `feature file with legacy data package — Legacy`() {
        // Heart of the rule: a feature module declaring
        // `com.po4yka.ratatoskr.data.…` is the exact shape the audit
        // is trying to eliminate.
        assertEquals(
            PackageClassification.Legacy,
            LegacyPackagePathDetector.classify(
                filePath = "feature/summary/src/commonMain/kotlin/Foo.kt",
                packageStatement = "com.po4yka.ratatoskr.data.repository",
            ),
        )
    }

    @Test
    fun `feature file with legacy domain package — Legacy`() {
        assertEquals(
            PackageClassification.Legacy,
            LegacyPackagePathDetector.classify(
                filePath = "feature/auth/src/commonMain/kotlin/Bar.kt",
                packageStatement = "com.po4yka.ratatoskr.domain.usecase",
            ),
        )
    }

    @Test
    fun `feature file with legacy presentation package — Legacy`() {
        assertEquals(
            PackageClassification.Legacy,
            LegacyPackagePathDetector.classify(
                filePath = "feature/settings/src/commonMain/kotlin/Baz.kt",
                packageStatement = "com.po4yka.ratatoskr.presentation.viewmodel",
            ),
        )
    }

    @Test
    fun `feature file with feature-scoped package — FeatureScoped`() {
        // The migration target. The check accepts this.
        assertEquals(
            PackageClassification.FeatureScoped,
            LegacyPackagePathDetector.classify(
                filePath = "feature/summary/src/commonMain/kotlin/Foo.kt",
                packageStatement = "com.po4yka.ratatoskr.feature.summary.domain.usecase",
            ),
        )
    }

    @Test
    fun `core file with legacy data package — Permitted`() {
        // The legacy tree under core/data is intentional: shared
        // infrastructure lives there. The audit only targets feature
        // modules, so core/* stays out of scope.
        assertEquals(
            PackageClassification.Permitted,
            LegacyPackagePathDetector.classify(
                filePath = "core/data/src/commonMain/kotlin/Foo.kt",
                packageStatement = "com.po4yka.ratatoskr.data.remote",
            ),
        )
    }

    @Test
    fun `core file with feature-scoped package — Permitted`() {
        // Unusual but not forbidden — core code may co-host shared
        // feature helpers under a feature subpackage. Not the
        // audit's concern.
        assertEquals(
            PackageClassification.Permitted,
            LegacyPackagePathDetector.classify(
                filePath = "core/common/src/commonMain/kotlin/Helper.kt",
                packageStatement = "com.po4yka.ratatoskr.feature.summary.domain.model",
            ),
        )
    }

    @Test
    fun `feature file with unrelated package — Other`() {
        // Something neither legacy nor feature-scoped (e.g. a typo or a
        // third-party shim). Surface as Other so the audit can flag
        // separately from the migration concern.
        assertEquals(
            PackageClassification.Other,
            LegacyPackagePathDetector.classify(
                filePath = "feature/summary/src/commonMain/kotlin/Foo.kt",
                packageStatement = "com.example.thirdparty.shim",
            ),
        )
    }

    @Test
    fun `windows-style path separators normalized`() {
        // CI on Windows reports backslashes; the predicate must agree
        // with Mac/Linux verdicts so the audit runs cross-platform.
        assertEquals(
            PackageClassification.Legacy,
            LegacyPackagePathDetector.classify(
                filePath = "feature\\summary\\src\\commonMain\\kotlin\\Foo.kt",
                packageStatement = "com.po4yka.ratatoskr.data.repository",
            ),
        )
    }

    @Test
    fun `blank file path — Other`() {
        // Defensive: empty path means we can't decide whether the
        // legacy tree restriction applies, so treat as Other.
        assertEquals(
            PackageClassification.Other,
            LegacyPackagePathDetector.classify(
                filePath = "",
                packageStatement = "com.po4yka.ratatoskr.data.repository",
            ),
        )
    }

    @Test
    fun `blank package statement — Other`() {
        assertEquals(
            PackageClassification.Other,
            LegacyPackagePathDetector.classify(
                filePath = "feature/summary/src/commonMain/kotlin/Foo.kt",
                packageStatement = "  ",
            ),
        )
    }

    @Test
    fun `classify is deterministic`() {
        val a =
            LegacyPackagePathDetector.classify(
                filePath = "feature/summary/src/commonMain/kotlin/Foo.kt",
                packageStatement = "com.po4yka.ratatoskr.data.repository",
            )
        val b =
            LegacyPackagePathDetector.classify(
                filePath = "feature/summary/src/commonMain/kotlin/Foo.kt",
                packageStatement = "com.po4yka.ratatoskr.data.repository",
            )
        assertEquals(a, b)
    }
}
