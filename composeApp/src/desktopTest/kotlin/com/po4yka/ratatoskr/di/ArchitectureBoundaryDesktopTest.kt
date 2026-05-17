package com.po4yka.ratatoskr.di

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Desktop-lane mirror of the api/domain transport-DTO boundary check.
 *
 * The fuller [ArchitectureBoundaryTest] runs only on the `androidHostTest`
 * source set, but the import-rule is platform-agnostic and worth enforcing on
 * the desktop CI lane too so a transport leak isn't only caught when Android
 * is built. The two checks must stay in sync — when this rule loosens or
 * tightens, update both.
 */
class ArchitectureBoundaryDesktopTest {
    private val forbiddenImports =
        listOf(
            "import com.po4yka.ratatoskr.data.remote",
            "import com.po4yka.ratatoskr.data.remote.dto",
        )

    @Test
    fun `domain and api layers do not import transport types`() {
        val repoRoot = findRepoRoot()
        val scanRoots =
            listOf(
                repoRoot.resolve("feature"),
                repoRoot.resolve("core/common"),
                repoRoot.resolve("core/data"),
            )

        val violations =
            scanRoots
                .filter { Files.exists(it) }
                .flatMap { root ->
                    Files.walk(root).use { paths: Stream<Path> ->
                        paths
                            .filter { path ->
                                path.extension == "kt" &&
                                    (
                                        path.toString().contains("/domain/") ||
                                            path.toString().contains("/api/")
                                    ) &&
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
            "Forbidden transport imports found in domain or api sources:\n${violations.joinToString("\n")}",
        )
    }

    private fun findRepoRoot(): Path {
        var current = Path.of("").toAbsolutePath()
        while (current.name != "ratatoskr-client") {
            current = current.parent ?: error("Could not locate repository root")
        }
        return current
    }
}
