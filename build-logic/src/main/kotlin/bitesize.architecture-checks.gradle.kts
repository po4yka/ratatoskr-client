import com.po4yka.bitesizereader.buildlogic.VerifyArchitectureBoundariesTask

val verifyArchitectureBoundaries =
    tasks.register<VerifyArchitectureBoundariesTask>("verifyArchitectureBoundaries") {
        projectRootPath.set(layout.projectDirectory.asFile.absolutePath)

        screenFiles.from(
            layout.projectDirectory
                .dir("composeApp/src/commonMain/kotlin")
                .asFileTree
                .matching {
                    include("**/ui/screens/**/*.kt")
                    exclude("**/build/**")
                },
        )

        navigationFiles.from(
            layout.projectDirectory
                .dir("composeApp/src/commonMain/kotlin")
                .asFileTree
                .matching {
                    include("**/presentation/navigation/**/*.kt")
                    exclude("**/build/**")
                },
            layout.projectDirectory
                .dir("feature")
                .asFileTree
                .matching {
                    include("**/src/commonMain/kotlin/**/presentation/navigation/**/*.kt")
                    exclude("**/build/**")
                },
        )

        shellFiles.from(
            layout.projectDirectory
                .dir("composeApp/src/commonMain/kotlin")
                .asFileTree
                .matching {
                    include("**/presentation/navigation/**/*.kt")
                    include("**/app/**/*.kt")
                    exclude("**/build/**")
                },
        )

        featureFiles.from(
            layout.projectDirectory
                .dir("feature")
                .asFileTree
                .matching {
                    include("**/src/commonMain/kotlin/**/*.kt")
                    exclude("**/build/**")
                },
        )

        legacyFiles.from(
            layout.projectDirectory.file("shared/src/commonMain/kotlin/com/po4yka/bitesizereader/di/AppModule.kt"),
        )
    }

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named(verifyArchitectureBoundaries.name))
    }
}
