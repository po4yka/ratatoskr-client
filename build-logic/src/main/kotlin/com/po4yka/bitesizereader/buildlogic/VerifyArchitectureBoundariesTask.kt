package com.po4yka.bitesizereader.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class VerifyArchitectureBoundariesTask : DefaultTask() {
    @get:Input
    abstract val projectRootPath: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val screenFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val navigationFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val shellFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val featureFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val legacyFiles: ConfigurableFileCollection

    init {
        group = "verification"
        description = "Rejects direct DI in routed UI and cross-feature implementation imports."
    }

    @TaskAction
    fun verify() {
        val violations = mutableListOf<String>()
        val projectPath = java.io.File(projectRootPath.get()).toPath()

        fun relativePath(file: java.io.File): String = projectPath.relativize(file.toPath()).toString().replace('\\', '/')

        val screenSourceFiles =
            screenFiles.files
                .filter { it.exists() && it.extension == "kt" }
                .sortedBy(::relativePath)
                .map { file ->
                    SourceFile(
                        path = relativePath(file),
                        content = file.readText(),
                    )
                }
        violations += ArchitectureBoundaryRules.findComposableDiViolations(screenSourceFiles)

        val featureSourceFiles =
            featureFiles.files
                .filter { it.exists() && it.extension == "kt" }
                .sortedBy(::relativePath)
                .map { file ->
                    SourceFile(
                        path = relativePath(file),
                        content = file.readText(),
                    )
                }
        val featureTypeOwners = ArchitectureBoundaryRules.buildFeatureTypeOwners(featureSourceFiles)

        val directDiSourceFiles =
            (navigationFiles.files + shellFiles.files)
                .filter { it.exists() && it.extension == "kt" }
                .distinctBy(::relativePath)
                .sortedBy(::relativePath)
                .map { file ->
                    SourceFile(
                        path = relativePath(file),
                        content = file.readText(),
                    )
                }
        violations += ArchitectureBoundaryRules.findDirectDiViolations(directDiSourceFiles)

        val shellSourceFiles =
            shellFiles.files
                .filter { it.exists() && it.extension == "kt" }
                .sortedBy(::relativePath)
                .map { file ->
                    SourceFile(
                        path = relativePath(file),
                        content = file.readText(),
                    )
                }
        violations += ArchitectureBoundaryRules.findShellBoundaryViolations(shellSourceFiles, featureTypeOwners)
        violations += ArchitectureBoundaryRules.findFeatureBoundaryViolations(featureSourceFiles, featureTypeOwners)

        legacyFiles.files
            .filter { it.exists() }
            .sortedBy(::relativePath)
            .forEach { file ->
                violations += "Legacy DI: ${relativePath(file)} should be removed after feature module extraction"
            }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("Architecture boundary checks failed:")
                    violations.forEach { appendLine(" - $it") }
                },
            )
        }
    }

}
