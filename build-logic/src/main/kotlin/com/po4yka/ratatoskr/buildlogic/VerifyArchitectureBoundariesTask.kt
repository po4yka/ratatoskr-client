package com.po4yka.ratatoskr.buildlogic

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
    abstract val sourceFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val shellFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val featureFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildFiles: ConfigurableFileCollection

    init {
        group = "verification"
        description = "Rejects direct DI in routed UI and illegal module edges."
    }

    @TaskAction
    fun verify() {
        val violations = mutableListOf<String>()
        val projectPath = java.io.File(projectRootPath.get()).toPath()

        fun relativePath(file: java.io.File): String = projectPath.relativize(file.toPath()).toString().replace('\\', '/')

        fun load(files: Set<java.io.File>, extension: String): List<SourceFile> =
            files
                .filter { it.exists() && it.extension == extension }
                .distinctBy(::relativePath)
                .sortedBy(::relativePath)
                .map { file ->
                    SourceFile(
                        path = relativePath(file),
                        content = file.readText(),
                    )
                }

        val screenSourceFiles = load(screenFiles.files, "kt")
        violations += ArchitectureBoundaryRules.findComposableDiViolations(screenSourceFiles)

        val allSourceFiles = load(sourceFiles.files, "kt")
        violations += ArchitectureBoundaryRules.findDirectDiViolations(allSourceFiles)
        violations += ArchitectureBoundaryRules.findComposeAppFeatureUiViolations(allSourceFiles)
        violations += ArchitectureBoundaryRules.findDiManagedRouteRegistrationViolations(allSourceFiles)
        violations += ArchitectureBoundaryRules.findRawAppRouteCreationViolations(allSourceFiles)

        val featureSourceFiles = load(featureFiles.files, "kt")
        val featureTypeOwners = ArchitectureBoundaryRules.buildFeatureTypeOwners(featureSourceFiles)
        val shellSourceFiles = load(shellFiles.files, "kt")
        violations += ArchitectureBoundaryRules.findShellBoundaryViolations(shellSourceFiles, featureTypeOwners)
        violations += ArchitectureBoundaryRules.findShellRouteUiImportViolations(shellSourceFiles)
        violations += ArchitectureBoundaryRules.findFeatureBoundaryViolations(featureSourceFiles, featureTypeOwners)

        val buildSourceFiles = load(buildFiles.files, "kts")
        violations += ArchitectureBoundaryRules.findModuleDependencyViolations(buildSourceFiles)

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
