import com.po4yka.ratatoskr.buildlogic.VerifyArchitectureBoundariesTask

fun allSourceTree(path: String) =
    layout.projectDirectory
        .dir(path)
        .asFileTree
        .matching {
            include("**/*.kt")
            exclude("**/build/**")
        }

fun productionSourceTree(path: String) =
    layout.projectDirectory
        .dir(path)
        .asFileTree
        .matching {
            include("**/*.kt")
            exclude("**/build/**")
            exclude("**/commonTest/**/*.kt")
            exclude("**/androidUnitTest/**/*.kt")
            exclude("**/androidInstrumentedTest/**/*.kt")
            exclude("**/androidHostTest/**/*.kt")
            exclude("**/androidDeviceTest/**/*.kt")
            exclude("**/iosTest/**/*.kt")
            exclude("**/desktopTest/**/*.kt")
        }

val verifyArchitectureBoundaries =
    tasks.register<VerifyArchitectureBoundariesTask>("verifyArchitectureBoundaries") {
        projectRootPath.set(layout.projectDirectory.asFile.absolutePath)

        screenFiles.from(
            layout.projectDirectory
                .dir("composeApp/src")
                .asFileTree
                .matching {
                    include("**/ui/screens/**/*.kt")
                    exclude("**/build/**")
                },
        )

        sourceFiles.from(
            allSourceTree("composeApp/src"),
            allSourceTree("androidApp/src"),
            allSourceTree("core/common/src"),
            allSourceTree("core/data/src"),
            allSourceTree("core/navigation/src"),
            allSourceTree("core/ui/src"),
            allSourceTree("feature/auth/src"),
            allSourceTree("feature/collections/src"),
            allSourceTree("feature/digest/src"),
            allSourceTree("feature/settings/src"),
            allSourceTree("feature/summary/src"),
            allSourceTree("feature/sync/src"),
        )

        shellFiles.from(
            layout.projectDirectory
                .dir("composeApp/src")
                .asFileTree
                .matching {
                    include("**/*.kt")
                    exclude("**/commonTest/**/*.kt")
                    exclude("**/androidUnitTest/**/*.kt")
                    exclude("**/androidInstrumentedTest/**/*.kt")
                    exclude("**/androidHostTest/**/*.kt")
                    exclude("**/androidDeviceTest/**/*.kt")
                    exclude("**/iosTest/**/*.kt")
                    exclude("**/desktopTest/**/*.kt")
                    exclude("**/build/**")
                },
        )

        featureFiles.from(
            productionSourceTree("feature/auth/src"),
            productionSourceTree("feature/collections/src"),
            productionSourceTree("feature/digest/src"),
            productionSourceTree("feature/settings/src"),
            productionSourceTree("feature/summary/src"),
            productionSourceTree("feature/sync/src"),
        )

        buildFiles.from(
            layout.projectDirectory.file("composeApp/build.gradle.kts"),
            layout.projectDirectory.file("androidApp/build.gradle.kts"),
            layout.projectDirectory.file("core/common/build.gradle.kts"),
            layout.projectDirectory.file("core/data/build.gradle.kts"),
            layout.projectDirectory.file("core/navigation/build.gradle.kts"),
            layout.projectDirectory.file("core/ui/build.gradle.kts"),
            layout.projectDirectory.file("feature/auth/build.gradle.kts"),
            layout.projectDirectory.file("feature/collections/build.gradle.kts"),
            layout.projectDirectory.file("feature/digest/build.gradle.kts"),
            layout.projectDirectory.file("feature/settings/build.gradle.kts"),
            layout.projectDirectory.file("feature/summary/build.gradle.kts"),
            layout.projectDirectory.file("feature/sync/build.gradle.kts"),
        )
    }

val verifyNoMaterial3 =
    tasks.register("verifyNoMaterial3") {
        group = "verification"
        description =
            "Fails the build if any shared Kotlin source imports androidx.compose.material3.*. " +
                "Frost forbids Material 3 in commonMain, core/ui, and feature modules."

        val sources =
            files(
                allSourceTree("core/ui/src"),
                allSourceTree("feature/auth/src"),
                allSourceTree("feature/collections/src"),
                allSourceTree("feature/digest/src"),
                allSourceTree("feature/settings/src"),
                allSourceTree("feature/summary/src"),
                allSourceTree("feature/sync/src"),
                allSourceTree("composeApp/src"),
            )
        inputs.files(sources).withPropertyName("frostScannedSources")
        val designMdPath = layout.projectDirectory.file("DESIGN.md").asFile.absolutePath

        doLast {
            val violations =
                sources.asFileTree.files
                    .filter { it.extension == "kt" }
                    .filter { kt ->
                        kt.useLines { lines ->
                            lines.any { it.contains("androidx.compose.material3.") }
                        }
                    }
            if (violations.isNotEmpty()) {
                val message =
                    buildString {
                        appendLine("Frost violation: Material 3 imports are banned. See $designMdPath.")
                        violations.forEach { f -> appendLine("  - ${f.absolutePath}") }
                    }
                throw GradleException(message)
            }
        }
    }

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named(verifyArchitectureBoundaries.name))
        dependsOn(rootProject.tasks.named(verifyNoMaterial3.name))
    }
}
