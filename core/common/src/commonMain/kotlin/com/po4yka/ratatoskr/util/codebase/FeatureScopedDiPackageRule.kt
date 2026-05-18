package com.po4yka.ratatoskr.util.codebase

/**
 * Pure validator that pins the migration target for the
 * move-feature-di-packages-to-feature-scoped-namespaces audit: every
 * feature's DI bindings file must declare
 * `package com.po4yka.ratatoskr.feature.<name>.di` rather than the
 * pre-migration shared `com.po4yka.ratatoskr.di`.
 *
 * The shared-package shape works on JVM/Android but is fragile for iOS
 * framework export (collides with KSP-generated module classes if file
 * names ever align) and confuses tooling. Anchoring each module at its
 * own subpackage removes that fragility.
 *
 * Pure, side-effect-free, deterministic.
 */
object FeatureScopedDiPackageRule {
    private const val FEATURE_PREFIX = "com.po4yka.ratatoskr.feature."
    private const val DI_LEAF = ".di"

    /** Verdict for a single DI file's package declaration. */
    sealed interface Outcome {
        data object Valid : Outcome

        data class WrongPackage(val expected: String, val actual: String) : Outcome

        data object MissingFeatureName : Outcome

        data object Blank : Outcome
    }

    fun validate(
        packageStatement: String,
        featureName: String,
    ): Outcome {
        val pkg = packageStatement.trim()
        if (pkg.isEmpty()) return Outcome.Blank
        val name = featureName.trim()
        if (name.isEmpty()) return Outcome.MissingFeatureName
        val expected = FEATURE_PREFIX + name + DI_LEAF
        return if (pkg == expected) Outcome.Valid else Outcome.WrongPackage(expected = expected, actual = pkg)
    }
}
