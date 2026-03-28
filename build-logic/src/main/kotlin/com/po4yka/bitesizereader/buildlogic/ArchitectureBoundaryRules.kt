package com.po4yka.bitesizereader.buildlogic

internal data class SourceFile(
    val path: String,
    val content: String,
)

internal object ArchitectureBoundaryRules {
    fun findComposableDiViolations(screenFiles: List<SourceFile>): List<String> =
        screenFiles
            .filter { ComposableDiPattern.containsMatchIn(it.content) }
            .map { "Composable DI: ${it.path} must not call koinInject() directly" }

    fun findDirectDiViolations(files: List<SourceFile>): List<String> =
        files
            .filterNot { isAllowedDirectDiPath(it.path) }
            .filter { DirectKoinResolutionPattern.containsMatchIn(it.content) }
            .map { "Direct DI: ${it.path} must not resolve dependencies from Koin directly" }

    fun buildFeatureTypeOwners(files: List<SourceFile>): Map<String, String> =
        buildMap {
            files.forEach { file ->
                val feature = featureNameFromPath(file.path) ?: return@forEach
                val packageName = PackagePattern.find(file.content)?.groupValues?.get(1) ?: return@forEach
                TypeDeclarationPattern.findAll(file.content).forEach { match ->
                    put("$packageName.${match.groupValues[1]}", feature)
                }
            }
        }

    fun findShellBoundaryViolations(
        shellFiles: List<SourceFile>,
        featureTypeOwners: Map<String, String>,
    ): List<String> =
        shellFiles.flatMap { file ->
            scanImports(file.content).mapNotNull { importedType ->
                val ownerFeature = featureTypeOwners[importedType] ?: return@mapNotNull null
                if (FeatureImplementationImportPattern.containsMatchIn(importedType)) {
                    "Shell boundary: ${file.path} must not import feature implementation type $importedType from $ownerFeature"
                } else {
                    null
                }
            }
        }

    fun findFeatureBoundaryViolations(
        featureFiles: List<SourceFile>,
        featureTypeOwners: Map<String, String> = buildFeatureTypeOwners(featureFiles),
    ): List<String> =
        featureFiles.flatMap { file ->
            val currentFeature = featureNameFromPath(file.path) ?: return@flatMap emptyList()
            scanImports(file.content).mapNotNull { importedType ->
                val ownerFeature = featureTypeOwners[importedType] ?: return@mapNotNull null
                if (ownerFeature != currentFeature && FeatureImplementationImportPattern.containsMatchIn(importedType)) {
                    "Feature boundary: ${file.path} must not import $importedType from feature/$ownerFeature implementation"
                } else {
                    null
                }
            }
        }

    fun scanImports(content: String): List<String> =
        ImportPattern.findAll(content)
            .map { match -> match.groupValues[1].substringBefore(" as ").trim() }
            .filterNot { it.endsWith(".*") }
            .toList()

    fun featureNameFromPath(path: String): String? =
        path.split('/')
            .windowed(size = 2, step = 1)
            .firstOrNull { (a, _) -> a == "feature" }
            ?.get(1)

    fun isAllowedDirectDiPath(path: String): Boolean =
        path == "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/App.kt" ||
            path == "composeApp/src/commonMain/kotlin/com/po4yka/bitesizereader/app/AppCompositionRoot.kt" ||
            path.contains("/di/") ||
            path.contains("Worker") ||
            path.contains("Widget")

    private val ComposableDiPattern = Regex("""\bkoinInject\(""")
    private val DirectKoinResolutionPattern =
        Regex(
            """\bKoinComponent\b|\binject\(|\bkoin\.(?:get|getAll)\s*(?:<|\()|\bgetKoin\(\)\.(?:get|getAll)\s*(?:<|\()""",
        )
    private val FeatureImplementationImportPattern = Regex("""\.((data)|(presentation))\.""")
    private val PackagePattern = Regex("""(?m)^\s*package\s+([A-Za-z0-9_.]+)""")
    private val ImportPattern = Regex("""(?m)^\s*import\s+([A-Za-z0-9_.* ]+)""")
    private val TypeDeclarationPattern =
        Regex(
            """(?m)^\s*(?:@[A-Za-z0-9_().,\s:"]+\s*)*(?:(?:public|internal|private|sealed|data|enum|annotation|expect|actual|abstract|open|value|inline|fun)\s+)*(?:class|interface|object|typealias)\s+([A-Za-z_][A-Za-z0-9_]*)""",
        )
}
