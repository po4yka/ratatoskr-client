package com.po4yka.ratatoskr.di

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.extension
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Test
import kotlin.test.assertTrue

class ArchitectureBoundaryTest {
    private val forbiddenImports =
        listOf(
            "import com.po4yka.ratatoskr.data.remote",
            "import com.po4yka.ratatoskr.data.remote.dto",
        )

    @Test
    fun `domain layer does not import transport types`() {
        val repoRoot = findRepoRoot()
        val domainRoots =
            listOf(
                repoRoot.resolve("feature"),
                repoRoot.resolve("core/common"),
                repoRoot.resolve("core/data"),
            )

        val violations =
            domainRoots
                .filter { Files.exists(it) }
                .flatMap { root ->
                    Files.walk(root).use { paths: Stream<Path> ->
                        paths
                            .filter { path ->
                                path.extension == "kt" &&
                                    path.toString().contains("/domain/") &&
                                    !path.toString().contains("/build/")
                            }
                            .map { path ->
                                val content = path.readText()
                                val offendingImport = forbiddenImports.firstOrNull(content::contains)
                                offendingImport?.let { "${repoRoot.relativize(path)} -> $it" }
                            }
                            .filter { it != null }
                            .map { it!! }
                            .toList()
                    }
                }

        assertTrue(
            violations.isEmpty(),
            "Forbidden transport imports found in domain sources:\n${violations.joinToString("\n")}",
        )
    }

    @Test
    fun `all platform bootstraps list the same split modules`() {
        val repoRoot = findRepoRoot()
        val expectedGeneratedModules =
            listOf(
                "com_po4yka_ratatoskr_di_CoreCommonModule",
                "com_po4yka_ratatoskr_di_NetworkModule",
                "com_po4yka_ratatoskr_di_DatabaseModule",
                "com_po4yka_ratatoskr_di_AuthFeatureModule",
                "com_po4yka_ratatoskr_di_CollectionsFeatureModule",
                "com_po4yka_ratatoskr_di_DigestFeatureModule",
                "com_po4yka_ratatoskr_di_SettingsFeatureModule",
                "com_po4yka_ratatoskr_di_SummaryFeatureModule",
                "com_po4yka_ratatoskr_di_SyncFeatureModule",
            )

        val bootstrapFiles =
            listOf(
                repoRoot.resolve("composeApp/src/androidMain/kotlin/com/po4yka/ratatoskr/di/KoinInitializer.kt"),
                repoRoot.resolve("composeApp/src/desktopMain/kotlin/com/po4yka/ratatoskr/di/KoinInitializer.kt"),
                repoRoot.resolve(
                    "composeApp/src/iosArm64Main/kotlin/com/po4yka/ratatoskr/di/IosCommonModules.kt",
                ),
                repoRoot.resolve(
                    "composeApp/src/iosSimulatorArm64Main/kotlin/com/po4yka/ratatoskr/di/IosCommonModules.kt",
                ),
                repoRoot.resolve(
                    "composeApp/src/iosX64Main/kotlin/com/po4yka/ratatoskr/di/IosCommonModules.kt",
                ),
            )

        val violations =
            bootstrapFiles.mapNotNull { path ->
                val content = path.readText()
                val missing = expectedGeneratedModules.filterNot(content::contains)
                if (missing.isEmpty()) {
                    null
                } else {
                    "${repoRoot.relativize(path)} missing ${missing.joinToString()}"
                }
            }

        assertTrue(
            violations.isEmpty(),
            "Platform bootstrap module lists drifted:\n${violations.joinToString("\n")}",
        )
    }

    @Test
    fun `appModules aggregates generated and binding modules`() {
        val repoRoot = findRepoRoot()
        val content =
            repoRoot.resolve("composeApp/src/commonMain/kotlin/com/po4yka/ratatoskr/di/AppModules.kt")
                .readText()

        val expectedEntries =
            listOf(
                "GeneratedAppModules.coreCommon",
                "GeneratedAppModules.network",
                "GeneratedAppModules.database",
                "GeneratedAppModules.auth",
                "GeneratedAppModules.collections",
                "GeneratedAppModules.digest",
                "GeneratedAppModules.settings",
                "GeneratedAppModules.summary",
                "GeneratedAppModules.sync",
                "authFeatureBindingsModule",
                "collectionsFeatureBindingsModule",
                "digestFeatureBindingsModule",
                "settingsFeatureBindingsModule",
                "summaryFeatureBindingsModule",
                "syncFeatureBindingsModule",
            )

        val missing = expectedEntries.filterNot(content::contains)
        assertTrue(
            missing.isEmpty(),
            "AppModules aggregation drifted; missing ${missing.joinToString()}",
        )
    }

    @Test
    fun `features do not import other feature presentation or data types`() {
        val repoRoot = findRepoRoot()
        val featureRoot = repoRoot.resolve("feature")
        val featureFiles =
            Files.walk(featureRoot).use { paths: Stream<Path> ->
                paths
                    .filter { path ->
                        path.extension == "kt" &&
                            path.toString().contains("/src/commonMain/kotlin/") &&
                            !path.toString().contains("/build/")
                    }
                    .toList()
            }

        val typeOwners = buildFeatureTypeOwners(featureFiles)
        val violations =
            featureFiles.flatMap { path ->
                val currentFeature = featureName(path) ?: return@flatMap emptyList()
                scanImports(path.readText()).mapNotNull { importedType ->
                    val ownerFeature = typeOwners[importedType] ?: return@mapNotNull null
                    if (
                        ownerFeature != currentFeature &&
                        (importedType.contains(".presentation.") || importedType.contains(".data."))
                    ) {
                        "${repoRoot.relativize(path)} -> $importedType from feature/$ownerFeature"
                    } else {
                        null
                    }
                }
            }

        assertTrue(
            violations.isEmpty(),
            "Cross-feature implementation imports found:\n${violations.joinToString("\n")}",
        )
    }

    private fun findRepoRoot(): Path {
        var current = Path.of("").toAbsolutePath()
        while (current.name != "ratatoskr-client") {
            current = current.parent ?: error("Could not locate repository root")
        }
        return current
    }

    private fun buildFeatureTypeOwners(files: List<Path>): Map<String, String> =
        buildMap {
            files.forEach { path ->
                val feature = featureName(path) ?: return@forEach
                val content = path.readText()
                val packageName = PACKAGE_PATTERN.find(content)?.groupValues?.get(1) ?: return@forEach
                TYPE_DECLARATION_PATTERN.findAll(content).forEach { match ->
                    put("$packageName.${match.groupValues[1]}", feature)
                }
            }
        }

    private fun featureName(path: Path): String? {
        val segments = path.invariantSeparatorsPathString.split('/')
        val featureIndex = segments.indexOf("feature")
        return if (featureIndex >= 0 && featureIndex + 1 < segments.size) segments[featureIndex + 1] else null
    }

    private fun scanImports(content: String): List<String> =
        IMPORT_PATTERN.findAll(content)
            .map { it.groupValues[1].substringBefore(" as ").trim() }
            .filterNot { it.endsWith(".*") }
            .toList()

    private companion object {
        val PACKAGE_PATTERN = Regex("""(?m)^\s*package\s+([A-Za-z0-9_.]+)""")
        val IMPORT_PATTERN = Regex("""(?m)^\s*import\s+([A-Za-z0-9_.* ]+)""")
        val TYPE_DECLARATION_PATTERN =
            Regex(
                """
                (?m)^\s*
                (?:@[A-Za-z0-9_().,\s:"]+\s*)*
                (?:(?:public|internal|private|sealed|data|enum|annotation|expect|actual|abstract|open|value|inline|fun)\s+)*
                (?:class|interface|object|typealias)\s+([A-Za-z_][A-Za-z0-9_]*)
                """.trimIndent(),
            )
    }
}
