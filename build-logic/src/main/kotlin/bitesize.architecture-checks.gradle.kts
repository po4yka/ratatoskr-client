import com.po4yka.bitesizereader.buildlogic.VerifyArchitectureBoundariesTask

val verifyArchitectureBoundaries =
    tasks.register<VerifyArchitectureBoundariesTask>("verifyArchitectureBoundaries") {
        rootDirectory.set(layout.projectDirectory)

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
                .dir("shared/src/commonMain/kotlin")
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

        legacyFiles.from(
            layout.projectDirectory.file("shared/src/commonMain/kotlin/com/po4yka/bitesizereader/di/AppModule.kt"),
            layout.projectDirectory.file("shared/src/commonMain/kotlin/com/po4yka/bitesizereader/di/RepositoryModule.kt"),
            layout.projectDirectory.file("shared/src/commonMain/kotlin/com/po4yka/bitesizereader/di/UseCaseModule.kt"),
        )
    }

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named(verifyArchitectureBoundaries.name))
    }
}
