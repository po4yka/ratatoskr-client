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
    abstract val legacyFiles: ConfigurableFileCollection

    init {
        group = "verification"
        description = "Rejects direct DI in route composables and Koin-backed navigation components."
    }

    @TaskAction
    fun verify() {
        val violations = mutableListOf<String>()
        val projectPath = java.io.File(projectRootPath.get()).toPath()

        fun relativePath(file: java.io.File): String = projectPath.relativize(file.toPath()).toString().replace('\\', '/')

        screenFiles.files
            .sortedBy(::relativePath)
            .forEach { file ->
                if (ComposableDiPattern.containsMatchIn(file.readText())) {
                    violations += "Composable DI: ${relativePath(file)} must not call koinInject() directly"
                }
            }

        navigationFiles.files
            .sortedBy(::relativePath)
            .forEach { file ->
                if (NavigationDiPattern.containsMatchIn(file.readText())) {
                    violations += "Navigation DI: ${relativePath(file)} must not resolve dependencies from Koin directly"
                }
            }

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

    private companion object {
        val ComposableDiPattern = Regex("""\bkoinInject\(""")
        val NavigationDiPattern = Regex("""\bKoinComponent\b|\binject\(|org\.koin\.core\.component\.get""")
    }
}
