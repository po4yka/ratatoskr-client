package com.po4yka.ratatoskr.buildlogic

internal data class SourceFile(
    val path: String,
    val content: String,
)

internal data class OwnedType(
    val feature: String,
    val path: String,
)

internal object ArchitectureBoundaryRules {
    private val allowedFeatureProjectDependencies =
        mapOf(
            "feature/auth" to emptySet(),
            "feature/collections" to setOf("feature/sync"),
            "feature/digest" to setOf("feature/summary"),
            "feature/settings" to setOf("feature/auth", "feature/summary", "feature/sync"),
            "feature/summary" to setOf("feature/auth", "feature/collections", "feature/sync"),
            "feature/sync" to emptySet(),
        )

    private val allowedLegacyDocPaths =
        setOf(
            "shared/AGENTS.md",
            "docs/BUILD_MIGRATION_DEFERRED.md",
        )

    private val allowedComposeAppUiPaths =
        setOf(
            "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/ui/screens/MainScreen.kt",
        )

    fun findComposableDiViolations(screenFiles: List<SourceFile>): List<String> =
        screenFiles
            .filter { ComposableDiPattern.containsMatchIn(stripComments(it.content)) }
            .map { "Composable DI: ${it.path} must not call koinInject() directly" }

    fun findDirectDiViolations(files: List<SourceFile>): List<String> =
        files
            .filterNot { isAllowedDirectDiPath(it.path) }
            .filter { DirectKoinResolutionPattern.containsMatchIn(stripComments(it.content)) }
            .map { "Direct DI: ${it.path} must not resolve dependencies from Koin directly" }

    fun findComposeAppFeatureUiViolations(files: List<SourceFile>): List<String> =
        files
            .filter { it.path.startsWith("composeApp/src/") }
            .filterNot { isTestSourcePath(it.path) }
            .filter { it.path.contains("/ui/") }
            .filterNot { it.path in allowedComposeAppUiPaths }
            .map { "Shell UI: ${it.path} must live in core/ui or an owning feature module" }

    fun findShellRouteUiImportViolations(shellFiles: List<SourceFile>): List<String> =
        shellFiles.flatMap { file ->
            scanImports(file.content)
                .filter { it.startsWith("com.po4yka.ratatoskr.feature.") }
                .filter { ".ui.screens." in it || ".ui.auth." in it }
                .map { importedType ->
                    "Shell boundary: ${file.path} must not import feature route UI type $importedType"
                }
        }

    fun findDiManagedRouteRegistrationViolations(files: List<SourceFile>): List<String> =
        files
            .filter { DiManagedRouteRegistrationPattern.containsMatchIn(stripComments(it.content)) }
            .map { "Route registration: ${it.path} must export entries explicitly instead of binding them through Koin" }

    fun findRawAppRouteCreationViolations(files: List<SourceFile>): List<String> =
        files
            .filterNot { isAllowedRawAppRoutePath(it.path) }
            .filter { RawAppRouteCreationPattern.containsMatchIn(stripComments(it.content)) }
            .map {
                "Route ownership: ${it.path} must use owner feature route helpers instead of raw AppRoute(...) construction"
            }

    fun buildFeatureTypeOwners(files: List<SourceFile>): Map<String, OwnedType> =
        buildMap {
            files.forEach { file ->
                val feature = featureNameFromPath(file.path) ?: return@forEach
                val packageName = PackagePattern.find(file.content)?.groupValues?.get(1) ?: return@forEach
                TypeDeclarationPattern.findAll(file.content).forEach { match ->
                    put("$packageName.${match.groupValues[1]}", OwnedType(feature = feature, path = file.path))
                }
            }
        }

    fun findShellBoundaryViolations(
        shellFiles: List<SourceFile>,
        featureTypeOwners: Map<String, OwnedType>,
    ): List<String> =
        shellFiles.flatMap { file ->
            scanImports(file.content).mapNotNull { importedType ->
                val ownedType = featureTypeOwners[importedType] ?: return@mapNotNull null
                if (isShellImplementationPath(ownedType.path)) {
                    "Shell boundary: ${file.path} must not import feature implementation type $importedType from feature/${ownedType.feature}"
                } else {
                    null
                }
            }
        }

    fun findFeatureBoundaryViolations(
        featureFiles: List<SourceFile>,
        featureTypeOwners: Map<String, OwnedType> = buildFeatureTypeOwners(featureFiles),
    ): List<String> =
        featureFiles.flatMap { file ->
            val currentFeature = featureNameFromPath(file.path) ?: return@flatMap emptyList()
            scanImports(file.content).mapNotNull { importedType ->
                val ownedType = featureTypeOwners[importedType] ?: return@mapNotNull null
                if (ownedType.feature != currentFeature && isCrossFeatureImplementationPath(ownedType.path)) {
                    "Feature boundary: ${file.path} must not import $importedType from feature/${ownedType.feature} implementation"
                } else {
                    null
                }
            }
        }

    fun findModuleDependencyViolations(buildFiles: List<SourceFile>): List<String> {
        val moduleDependencies = parseProjectDependencies(buildFiles)
        val violations = mutableListOf<String>()

        moduleDependencies.forEach { (module, dependencies) ->
            if (module.startsWith("core/")) {
                dependencies
                    .filter { it.startsWith("feature/") }
                    .forEach { dependency ->
                        violations += "Module boundary: $module must not depend on $dependency"
                    }
            }

            if (module.startsWith("feature/")) {
                val allowedDependencies = allowedFeatureProjectDependencies[module].orEmpty()
                dependencies
                    .filter { it.startsWith("feature/") }
                    .filterNot(allowedDependencies::contains)
                    .forEach { dependency ->
                        violations += "Module boundary: $module must not depend on $dependency"
                    }
            }
        }

        return violations
    }

    fun findLegacyDocsViolations(docFiles: List<SourceFile>): List<String> =
        docFiles.flatMap { file ->
            if (file.path in allowedLegacyDocPaths) {
                emptyList()
            } else {
                LegacySharedDocPattern.findAll(file.content)
                    .map { match -> "Docs drift: ${file.path} still references legacy '${match.value}'" }
                    .toList()
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
        path == "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/App.kt" ||
            path == "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/app/AppCompositionRoot.kt" ||
            path == "composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/app/AppCompositionAssembly.kt" ||
            path == "composeApp/src/iosMain/kotlin/com/po4yka/ratatoskr/IosAppHost.kt" ||
            path.contains("/di/") ||
            path.contains("/worker/") ||
            path.contains("/widget/")

    private fun isAllowedRawAppRoutePath(path: String): Boolean =
        path == "core/navigation/src/commonMain/kotlin/com/po4yka/ratatoskr/navigation/MainNavigation.kt" ||
            (path.startsWith("feature/") && path.contains("/navigation/") && path.endsWith("Routes.kt"))

    private fun parseProjectDependencies(buildFiles: List<SourceFile>): Map<String, Set<String>> =
        buildMap {
            buildFiles.forEach { file ->
                val module = moduleNameFromBuildPath(file.path) ?: return@forEach
                val dependencies =
                    ProjectDependencyPattern.findAll(file.content)
                        .map { match -> match.groupValues[1].replace('.', '/') }
                        .toSet()
                put(module, dependencies)
            }
        }

    private fun moduleNameFromBuildPath(path: String): String? =
        when {
            path == "composeApp/build.gradle.kts" -> "composeApp"
            path == "androidApp/build.gradle.kts" -> "androidApp"
            path.startsWith("core/") && path.endsWith("/build.gradle.kts") -> path.removeSuffix("/build.gradle.kts")
            path.startsWith("feature/") && path.endsWith("/build.gradle.kts") -> path.removeSuffix("/build.gradle.kts")
            else -> null
        }

    private fun isShellImplementationPath(path: String): Boolean =
        path.contains("/data/") || path.contains("/di/") || path.contains("/presentation/viewmodel/")

    private fun isCrossFeatureImplementationPath(path: String): Boolean =
        path.contains("/data/") || path.contains("/di/") || path.contains("/presentation/")

    private fun isTestSourcePath(path: String): Boolean =
        path.contains("/commonTest/") ||
            path.contains("/androidUnitTest/") ||
            path.contains("/androidInstrumentedTest/") ||
            path.contains("/desktopTest/") ||
            path.contains("/iosTest/")

    private fun stripComments(content: String): String =
        content
            .replace(BlockCommentPattern, "")
            .replace(LineCommentPattern, "")

    private val ComposableDiPattern = Regex("""\bkoinInject\(""")
    private val DirectKoinResolutionPattern =
        Regex(
            """\bKoinComponent\b|\binject\(|\bkoin\.(?:get|getAll)\s*(?:<|\()|\bgetKoin\(\)\.(?:get|getAll)\s*(?:<|\()""",
        )
    private val LegacySharedDocPattern = Regex("""shared/|:shared:""")
    private val DiManagedRouteRegistrationPattern =
        Regex("""\b(?:single|factory)\s*<\s*(?:MainRouteEntry|AuthEntry)\s*>|\bbind\s+(?:MainRouteEntry|AuthEntry)::class""")
    private val RawAppRouteCreationPattern = Regex("""\bAppRoute\s*\(""")
    private val ProjectDependencyPattern =
        Regex(
            """(?m)\b(?:api|implementation|compileOnly|runtimeOnly|testImplementation|androidTestImplementation|debugImplementation|releaseImplementation)\s*\(\s*projects\.([A-Za-z0-9_.]+)\s*\)""",
        )
    private val PackagePattern = Regex("""(?m)^\s*package\s+([A-Za-z0-9_.]+)""")
    private val ImportPattern = Regex("""(?m)^\s*import\s+([A-Za-z0-9_.* ]+)""")
    private val LineCommentPattern = Regex("""(?m)//.*$""")
    private val BlockCommentPattern = Regex("""(?s)/\*.*?\*/""")
    private val TypeDeclarationPattern =
        Regex(
            """(?m)^\s*(?:@[A-Za-z0-9_().,\s:"]+\s*)*(?:(?:public|internal|private|sealed|data|enum|annotation|expect|actual|abstract|open|value|inline|fun)\s+)*(?:class|interface|object|typealias)\s+([A-Za-z_][A-Za-z0-9_]*)""",
        )
}
