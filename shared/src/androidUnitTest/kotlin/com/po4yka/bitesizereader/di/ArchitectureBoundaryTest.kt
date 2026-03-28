package com.po4yka.bitesizereader.di

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Test
import kotlin.test.assertTrue

class ArchitectureBoundaryTest {
    private val forbiddenImports =
        listOf(
            "import com.po4yka.bitesizereader.data.remote",
            "import com.po4yka.bitesizereader.data.remote.dto",
        )

    @Test
    fun `domain layer does not import transport types`() {
        val repoRoot = findRepoRoot()
        val domainRoots =
            listOf(
                repoRoot.resolve("feature"),
                repoRoot.resolve("core"),
                repoRoot.resolve("shared"),
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
        val expectedModules =
            listOf(
                "NetworkModule().module",
                "DatabaseModule().module",
                "AuthFeatureModule().module",
                "CollectionsFeatureModule().module",
                "DigestFeatureModule().module",
                "SettingsFeatureModule().module",
                "SummaryFeatureModule().module",
                "SyncFeatureModule().module",
            )

        val bootstrapFiles =
            listOf(
                repoRoot.resolve("shared/src/androidMain/kotlin/com/po4yka/bitesizereader/di/KoinInitializer.kt"),
                repoRoot.resolve("shared/src/desktopMain/kotlin/com/po4yka/bitesizereader/di/KoinInitializer.kt"),
                repoRoot.resolve("shared/src/iosArm64Main/kotlin/com/po4yka/bitesizereader/di/IosCommonModules.kt"),
                repoRoot.resolve("shared/src/iosSimulatorArm64Main/kotlin/com/po4yka/bitesizereader/di/IosCommonModules.kt"),
                repoRoot.resolve("shared/src/iosX64Main/kotlin/com/po4yka/bitesizereader/di/IosCommonModules.kt"),
            )

        val violations =
            bootstrapFiles.mapNotNull { path ->
                val content = path.readText()
                val missing = expectedModules.filterNot(content::contains)
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

    private fun findRepoRoot(): Path {
        var current = Path.of("").toAbsolutePath()
        while (current.name != "bite-size-reader-client") {
            current = current.parent ?: error("Could not locate repository root")
        }
        return current
    }
}
