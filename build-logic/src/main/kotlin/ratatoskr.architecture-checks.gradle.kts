import com.po4yka.ratatoskr.buildlogic.VerifyArchitectureBoundariesTask

fun allSourceTree(path: String) =
    layout.projectDirectory
        .dir(path)
        .asFileTree
        .matching {
            include("**/*.kt")
            exclude("**/build/**")
        }

fun productionSourceTree(path: String) =
    layout.projectDirectory
        .dir(path)
        .asFileTree
        .matching {
            include("**/*.kt")
            exclude("**/build/**")
            exclude("**/commonTest/**/*.kt")
            exclude("**/androidUnitTest/**/*.kt")
            exclude("**/androidInstrumentedTest/**/*.kt")
            exclude("**/androidHostTest/**/*.kt")
            exclude("**/androidDeviceTest/**/*.kt")
            exclude("**/iosTest/**/*.kt")
            exclude("**/desktopTest/**/*.kt")
        }

val verifyArchitectureBoundaries =
    tasks.register<VerifyArchitectureBoundariesTask>("verifyArchitectureBoundaries") {
        projectRootPath.set(layout.projectDirectory.asFile.absolutePath)

        screenFiles.from(
            layout.projectDirectory
                .dir("composeApp/src")
                .asFileTree
                .matching {
                    include("**/ui/screens/**/*.kt")
                    exclude("**/build/**")
                },
        )

        sourceFiles.from(
            allSourceTree("composeApp/src"),
            allSourceTree("androidApp/src"),
            allSourceTree("core/common/src"),
            allSourceTree("core/data/src"),
            allSourceTree("core/navigation/src"),
            allSourceTree("core/ui/src"),
            allSourceTree("feature/auth/src"),
            allSourceTree("feature/collections/src"),
            allSourceTree("feature/digest/src"),
            allSourceTree("feature/settings/src"),
            allSourceTree("feature/summary/src"),
            allSourceTree("feature/sync/src"),
        )

        shellFiles.from(
            layout.projectDirectory
                .dir("composeApp/src")
                .asFileTree
                .matching {
                    include("**/*.kt")
                    exclude("**/commonTest/**/*.kt")
                    exclude("**/androidUnitTest/**/*.kt")
                    exclude("**/androidInstrumentedTest/**/*.kt")
                    exclude("**/androidHostTest/**/*.kt")
                    exclude("**/androidDeviceTest/**/*.kt")
                    exclude("**/iosTest/**/*.kt")
                    exclude("**/desktopTest/**/*.kt")
                    exclude("**/build/**")
                },
        )

        featureFiles.from(
            productionSourceTree("feature/auth/src"),
            productionSourceTree("feature/collections/src"),
            productionSourceTree("feature/digest/src"),
            productionSourceTree("feature/settings/src"),
            productionSourceTree("feature/summary/src"),
            productionSourceTree("feature/sync/src"),
        )

        buildFiles.from(
            layout.projectDirectory.file("composeApp/build.gradle.kts"),
            layout.projectDirectory.file("androidApp/build.gradle.kts"),
            layout.projectDirectory.file("core/common/build.gradle.kts"),
            layout.projectDirectory.file("core/data/build.gradle.kts"),
            layout.projectDirectory.file("core/navigation/build.gradle.kts"),
            layout.projectDirectory.file("core/ui/build.gradle.kts"),
            layout.projectDirectory.file("feature/auth/build.gradle.kts"),
            layout.projectDirectory.file("feature/collections/build.gradle.kts"),
            layout.projectDirectory.file("feature/digest/build.gradle.kts"),
            layout.projectDirectory.file("feature/settings/build.gradle.kts"),
            layout.projectDirectory.file("feature/summary/build.gradle.kts"),
            layout.projectDirectory.file("feature/sync/build.gradle.kts"),
        )
    }

val verifyFrostPalette =
    tasks.register("verifyFrostPalette") {
        group = "verification"
        description =
            "Fails the build when shared Kotlin source declares a `Color(0x...)` literal " +
                "outside the Frost palette (INK / PAGE / SPARK / pure black / pure white). " +
                "Per-line opt-out with the `// frost-allow` magic comment."

        // Hex literals (with or without the `Color(...)` wrapper) that the
        // Frost palette legitimately uses. New tokens must be added here AND
        // to FrostColors.kt simultaneously.
        val allowedHex =
            setOf(
                "0xFF1C242C", // ink (light)
                "0xFFE8ECF0", // ink (dark)
                "0xFFF0F2F5", // page (light)
                "0xFF12161C", // page (dark)
                "0xFFDC3545", // spark
                "0xFF000000", // inkPure
                "0xFFFFFFFF", // pagePure
            )

        val sources =
            files(
                allSourceTree("core/ui/src"),
                allSourceTree("feature/auth/src"),
                allSourceTree("feature/collections/src"),
                allSourceTree("feature/digest/src"),
                allSourceTree("feature/settings/src"),
                allSourceTree("feature/summary/src"),
                allSourceTree("feature/sync/src"),
                allSourceTree("composeApp/src"),
            )
        inputs.files(sources).withPropertyName("frostPaletteScannedSources")
        val designMdPath = layout.projectDirectory.file("DESIGN.md").asFile.absolutePath

        val hexLiteralRegex = Regex("""Color\(\s*(0x[0-9A-Fa-f]{6,8})\b""")

        // The palette source-of-truth files declare every legitimate hex value;
        // they are excluded from the scan so the guard only catches consumer
        // code that reaches for a raw literal instead of the named tokens.
        val paletteSources =
            setOf(
                "core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/theme/FrostColors.kt",
                "core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/theme/Color.kt",
            )
        val rootDir = layout.projectDirectory.asFile

        doLast {
            val violations = mutableListOf<String>()
            sources.asFileTree.files
                .filter { it.extension == "kt" }
                .filter { kt ->
                    val rel = kt.relativeTo(rootDir).invariantSeparatorsPath
                    rel !in paletteSources
                }
                .forEach { kt ->
                    kt.useLines { lines ->
                        lines.forEachIndexed { idx, line ->
                            if ("frost-allow" in line) return@forEachIndexed
                            val match = hexLiteralRegex.find(line) ?: return@forEachIndexed
                            val literal = match.groupValues[1].uppercase()
                            // Normalize 6-digit forms to assume opaque alpha so the
                            // allowlist match is fair (0xRRGGBB → 0xFFRRGGBB).
                            val normalized =
                                if (literal.length == "0x".length + 6) "0xFF" + literal.substring(2) else literal
                            if (normalized !in allowedHex) {
                                violations.add("${kt.absolutePath}:${idx + 1}  ${line.trim()}")
                            }
                        }
                    }
                }
            if (violations.isNotEmpty()) {
                val message =
                    buildString {
                        appendLine("Frost palette violation: hex Color(0x...) outside the allowlist.")
                        appendLine("Allowed tokens: ${allowedHex.joinToString(", ")}.")
                        appendLine("Add `// frost-allow` on the offending line if it is a deliberate exception.")
                        appendLine("Canonical palette spec: $designMdPath.")
                        violations.forEach { appendLine("  - $it") }
                    }
                throw GradleException(message)
            }
        }
    }

val verifyDesignMd =
    tasks.register("verifyDesignMd") {
        group = "verification"
        description =
            "Validates that the DESIGN.md `components:` frontmatter list matches the " +
                "atoms shipped under core/ui/.../components/frost/. No-op when the list is " +
                "absent so the guard activates the moment DESIGN.md adopts the canon."

        val designMd = layout.projectDirectory.file("DESIGN.md")
        val frostDir =
            layout.projectDirectory.dir(
                "core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/frost",
            )
        inputs.file(designMd).withPropertyName("designMd")
        inputs.dir(frostDir).withPropertyName("frostAtomDir")

        doLast {
            val md = designMd.asFile.readText()
            // Pull the YAML frontmatter block at top of file.
            val frontmatter =
                Regex("""^---\n(.*?)\n---""", RegexOption.DOT_MATCHES_ALL)
                    .find(md)
                    ?.groupValues
                    ?.get(1)
                    ?: return@doLast
            // Look for `components:` followed by an indented `  - Name` list.
            val componentsBlock =
                Regex("""(?m)^components:\s*\n((?:[ \t]+-\s+\S.*\n?)+)""")
                    .find(frontmatter)
                    ?.groupValues
                    ?.get(1)
                    ?: return@doLast // No canon list yet → nothing to validate.

            val listed =
                componentsBlock
                    .lineSequence()
                    .mapNotNull { Regex("""^[ \t]+-\s+([A-Za-z0-9_]+)""").find(it)?.groupValues?.get(1) }
                    .toSortedSet()
            val onDisk =
                frostDir.asFile.listFiles()
                    .orEmpty()
                    .filter { it.extension == "kt" }
                    .map { it.nameWithoutExtension }
                    .toSortedSet()

            val missingFromDoc = onDisk - listed
            val missingFromDisk = listed - onDisk
            if (missingFromDoc.isNotEmpty() || missingFromDisk.isNotEmpty()) {
                throw GradleException(
                    buildString {
                        appendLine("Frost canon drift between DESIGN.md and core/ui/.../components/frost/:")
                        if (missingFromDoc.isNotEmpty()) {
                            appendLine("  - Atoms on disk but not in DESIGN.md `components:` — $missingFromDoc")
                        }
                        if (missingFromDisk.isNotEmpty()) {
                            appendLine("  - Atoms in DESIGN.md `components:` but missing on disk — $missingFromDisk")
                        }
                    },
                )
            }
        }
    }

val verifyNoMaterial3 =
    tasks.register("verifyNoMaterial3") {
        group = "verification"
        description =
            "Fails the build if any shared Kotlin source imports androidx.compose.material3.*. " +
                "Frost forbids Material 3 in commonMain, core/ui, and feature modules."

        val sources =
            files(
                allSourceTree("core/ui/src"),
                allSourceTree("feature/auth/src"),
                allSourceTree("feature/collections/src"),
                allSourceTree("feature/digest/src"),
                allSourceTree("feature/settings/src"),
                allSourceTree("feature/summary/src"),
                allSourceTree("feature/sync/src"),
                allSourceTree("composeApp/src"),
            )
        inputs.files(sources).withPropertyName("frostScannedSources")
        val designMdPath = layout.projectDirectory.file("DESIGN.md").asFile.absolutePath

        doLast {
            val violations =
                sources.asFileTree.files
                    .filter { it.extension == "kt" }
                    .filter { kt ->
                        kt.useLines { lines ->
                            lines.any { it.contains("androidx.compose.material3.") }
                        }
                    }
            if (violations.isNotEmpty()) {
                val message =
                    buildString {
                        appendLine("Frost violation: Material 3 imports are banned. See $designMdPath.")
                        violations.forEach { f -> appendLine("  - ${f.absolutePath}") }
                    }
                throw GradleException(message)
            }
        }
    }

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named(verifyArchitectureBoundaries.name))
        dependsOn(rootProject.tasks.named(verifyNoMaterial3.name))
        dependsOn(rootProject.tasks.named(verifyFrostPalette.name))
        dependsOn(rootProject.tasks.named(verifyDesignMd.name))
    }
}
