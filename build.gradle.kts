plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.ktlint) apply true
    alias(libs.plugins.detekt) apply true
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.0.1")
        android.set(true)
        outputColorName.set("RED")

        // Exclude generated files
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude { element -> element.file.path.contains("generated/") }
        }
    }

    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>().configureEach {
        exclude("**/build/**", "**/generated/**")
    }

    // Detekt configuration
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(files("$rootDir/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = false
        parallel = true
        ignoreFailures = false
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "11"

        // Include source files
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**", "**/generated/**", "**/resources/**")

        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
            md.required.set(false)
        }
    }

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
    }
}
