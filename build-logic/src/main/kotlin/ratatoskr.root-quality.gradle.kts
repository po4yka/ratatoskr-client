subprojects {
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    pluginManager.apply("io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.0.1")
        android.set(true)
        outputColorName.set("RED")

        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude { element -> element.file.path.contains("generated/") }
        }
    }

    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>().configureEach {
        exclude("**/build/**", "**/generated/**")
        tasks.findByName("kspCommonMainKotlinMetadata")?.let { dependsOn(it) }
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(files("$rootDir/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = false
        parallel = true
        ignoreFailures = false
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "17"
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
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    }
}
