package com.po4yka.ratatoskr.util.codebase

/**
 * Pure validator for the string argument inside a Koin
 * `@ComponentScan("…")` annotation. Companion to
 * [FeatureScopedDiPackageRule] — that rule pins the *file's* package
 * declaration; this one pins the *scanner's* argument so the migration
 * does not leave a wrongly-anchored scan that silently widens the
 * resolved use-case set.
 *
 * Expected shape after the migration:
 *  - `com.po4yka.ratatoskr.feature.<name>`              — feature root
 *  - `com.po4yka.ratatoskr.feature.<name>.<subpath>`    — any subpath
 *
 * Anything else (the pre-migration shared
 * `com.po4yka.ratatoskr.domain.usecase`, a third-party shim, or a
 * wrong-feature-name typo) is surfaced via a distinct outcome.
 *
 * Pure, side-effect-free, deterministic.
 */
object DiComponentScanArgument {
    private const val FEATURE_PREFIX = "com.po4yka.ratatoskr.feature."

    /** Verdict for one `@ComponentScan` argument. */
    sealed interface Outcome {
        data object Valid : Outcome

        data class WrongFeatureName(val expected: String, val actual: String) : Outcome

        data object NotFeatureScoped : Outcome

        data object Blank : Outcome

        data object MissingFeatureName : Outcome
    }

    fun validate(
        annotationArg: String,
        featureName: String,
    ): Outcome {
        val arg = annotationArg.trim()
        val name = featureName.trim()
        return when {
            arg.isEmpty() -> Outcome.Blank
            name.isEmpty() -> Outcome.MissingFeatureName
            !arg.startsWith(FEATURE_PREFIX) -> Outcome.NotFeatureScoped
            else -> classifyAfterPrefix(afterPrefix = arg.removePrefix(FEATURE_PREFIX), expectedName = name)
        }
    }

    private fun classifyAfterPrefix(
        afterPrefix: String,
        expectedName: String,
    ): Outcome {
        if (afterPrefix.isEmpty()) return Outcome.NotFeatureScoped
        val actualName = afterPrefix.substringBefore(delimiter = '.')
        return if (actualName == expectedName) {
            Outcome.Valid
        } else {
            Outcome.WrongFeatureName(expected = expectedName, actual = actualName)
        }
    }
}
