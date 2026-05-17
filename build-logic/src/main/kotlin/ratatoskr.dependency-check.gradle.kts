import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

// CVE gate. Apply on the root project so `./gradlew dependencyCheckAggregate`
// scans the entire build's resolved dependency graph in one pass.
//
// Plugin is intentionally NOT wired into `check` — running the scan on every
// `./gradlew check` invocation would pull the NVD database on cold caches
// (a 10+ minute download). The CVE workflow at
// `.github/workflows/dependency-check.yml` invokes it on schedule and on
// dependency-relevant PRs, where it caches the NVD database.
plugins {
    id("org.owasp.dependencycheck")
}

extensions.configure<DependencyCheckExtension> {
    // Threshold for failure. Start at CVSS 9.0 (critical) so the initial
    // rollout does not block on noisy low-severity findings; tighten as the
    // suppression file matures.
    failBuildOnCVSS = 9.0f

    // Reports land under build/reports/dependency-check.
    formats = listOf("HTML", "JSON", "SARIF")

    // Curated suppressions for confirmed false positives.
    suppressionFile = "$rootDir/dependency-check-suppressions.xml"

    // NVD API key from env (set in CI as the `NVD_API_KEY` secret). Without
    // a key the plugin runs but is rate-limited.
    nvd.apply {
        apiKey = System.getenv("NVD_API_KEY") ?: providers.gradleProperty("nvdApiKey").orNull
    }

    // Skip configurations that don't produce shipped artefacts (e.g., test
    // toolchains) to keep the report focused on what reaches users.
    skipConfigurations =
        listOf(
            "ktlintRuleset",
            "ktlint",
            "detekt",
            "detektPlugins",
            "kotlinCompilerClasspath",
            "kotlinKlibCommonizerClasspath",
        )

    // Don't fail when an API call to the NVD or other data sources
    // intermittently times out — the workflow re-runs on schedule.
    failOnError = false
}
