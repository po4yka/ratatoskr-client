package com.po4yka.ratatoskr.util.codebase

/**
 * Verdict for a single Kotlin source file in the codebase. The legacy-
 * layout migration cares about feature modules only — the core modules are exempt.
 *
 *  - [Legacy]        — file is under `feature/` and still declares the
 *                      `com.po4yka.ratatoskr.{data,domain,presentation}`
 *                      tree. This is what the migration is removing.
 *  - [FeatureScoped] — file is under `feature/` and declares the new
 *                      `com.po4yka.ratatoskr.feature.<name>.*` tree.
 *  - [Permitted]     — file is outside `feature/`. The legacy tree is
 *                      intentional in the `core` modules; not the audit's concern.
 *  - [Other]         — anything else (third-party shims, defensive empty
 *                      inputs).
 */
enum class PackageClassification {
    Legacy,
    FeatureScoped,
    Permitted,
    Other,
}

/**
 * Pure (filePath, packageStatement) → [PackageClassification] verdict.
 * Powers the upcoming detekt rule / `./gradlew check` step that
 * enforces the feature-package refactor.
 *
 * Path normalization handles Windows-style backslash separators so a CI
 * run on Windows produces the same verdict as Mac/Linux.
 *
 * Pure, side-effect-free, deterministic.
 */
object LegacyPackagePathDetector {
    private const val FEATURE_PREFIX = "feature/"
    private const val LEGACY_PACKAGE_PREFIX = "com.po4yka.ratatoskr."
    private const val FEATURE_SCOPED_PREFIX = "com.po4yka.ratatoskr.feature."
    private val LEGACY_ROOTS = setOf("data", "domain", "presentation")

    fun classify(
        filePath: String,
        packageStatement: String,
    ): PackageClassification {
        if (filePath.isBlank() || packageStatement.isBlank()) return PackageClassification.Other
        val normalizedPath = filePath.replace(oldChar = '\\', newChar = '/')
        val isFeatureFile = normalizedPath.startsWith(FEATURE_PREFIX)
        val pkg = packageStatement.trim()
        val isFeatureScopedPkg = pkg.startsWith(FEATURE_SCOPED_PREFIX)
        val isLegacyPkg =
            pkg.startsWith(LEGACY_PACKAGE_PREFIX) &&
                !isFeatureScopedPkg &&
                pkg.removePrefix(LEGACY_PACKAGE_PREFIX).substringBefore(delimiter = '.') in LEGACY_ROOTS
        return when {
            !isFeatureFile -> PackageClassification.Permitted
            isFeatureScopedPkg -> PackageClassification.FeatureScoped
            isLegacyPkg -> PackageClassification.Legacy
            else -> PackageClassification.Other
        }
    }
}
