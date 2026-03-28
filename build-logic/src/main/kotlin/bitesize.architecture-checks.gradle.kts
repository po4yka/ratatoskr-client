import com.po4yka.bitesizereader.buildlogic.VerifyArchitectureBoundariesTask

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
            layout.projectDirectory
                .dir("core")
                .asFileTree
                .matching { include("**/build.gradle.kts") },
            layout.projectDirectory
                .dir("feature")
                .asFileTree
                .matching { include("**/build.gradle.kts") },
        )

        docFiles.from(
            layout.projectDirectory.file("README.md"),
            layout.projectDirectory.file("AGENTS.md"),
            layout.projectDirectory.file("shared/AGENTS.md"),
            layout.projectDirectory
                .dir("docs")
                .asFileTree
                .matching { include("**/*.md") },
        )
    }

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named(verifyArchitectureBoundaries.name))
    }
}
