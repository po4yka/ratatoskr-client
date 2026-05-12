import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File
import java.net.URI

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.openapiKmpGen)
    id("ratatoskr.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.openapi.kmp.gen.companion)
            api(libs.arrow.core)
            api(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.client.serialization)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }
}

extensions.configure<KotlinMultiplatformExtension> {
    sourceSets.named("commonMain") {
        kotlin.srcDir("src/commonMain/generated")
    }
}

kmpgen {
    spec(packageName = "com.po4yka.ratatoskr.api.generated") {
        specFile = file("src/commonMain/openapi/mobile_api.yaml")
        generateAllNamedSchemas = true
    }
}

// The kmpgen plugin auto-registers `build/generated/kmpgen` as a commonMain
// source dir. We don't want that — the production source is the *patched*
// copy at `src/commonMain/generated/`. Remove the auto-registered dir.
afterEvaluate {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.named("commonMain") {
            val unwanted = layout.buildDirectory.dir("generated/kmpgen").get().asFile
            kotlin.setSrcDirs(kotlin.srcDirs.filterNot { it == unwanted })
        }
    }
}

// ---------------------------------------------------------------------------
// Spec fetch + normalize + post-gen patch pipeline.
//
// Manual flow:
//
//   ./gradlew :core:api-generated:regenerateOpenApi
//
// after bumping tools/openapi.lock. This fetches the pinned upstream YAML,
// normalizes it (rewriting OAS 3.1 nullable unions and other shapes
// openapi-kmp-gen 1.3.0 mishandles), runs the generator, patches two known
// generator bugs (`url: String` parameter shadowing the Ktor request
// builder; non-deterministic timestamp in Api.kt), and copies the result
// into the checked-in `src/commonMain/generated/` tree.
//
// CI runs `checkOpenApiDrift` to verify the on-disk YAML matches the pin.
// ---------------------------------------------------------------------------

abstract class FetchOpenApiSpecTask : DefaultTask() {
    @get:InputFile abstract val lockFile: RegularFileProperty
    @get:InputFile abstract val normalizerScript: RegularFileProperty
    @get:OutputFile abstract val outputYaml: RegularFileProperty

    @TaskAction
    fun run() {
        @Suppress("UNCHECKED_CAST")
        val lock = JsonSlurper().parse(lockFile.get().asFile) as Map<String, String>
        val url = "https://raw.githubusercontent.com/${lock["repo"]}/${lock["ref"]}/${lock["path"]}"
        logger.lifecycle("openapi: fetching $url")
        val target = outputYaml.get().asFile
        target.parentFile.mkdirs()
        val tmp = File.createTempFile("openapi-", ".yaml")
        try {
            URI(url).toURL().openStream().use { input ->
                tmp.outputStream().use { output -> input.copyTo(output) }
            }
            val proc = ProcessBuilder(
                "python3",
                normalizerScript.get().asFile.absolutePath,
                tmp.absolutePath,
                target.absolutePath,
            )
                .redirectErrorStream(true)
                .start()
            val log = proc.inputStream.bufferedReader().readText()
            if (proc.waitFor() != 0) {
                throw GradleException("normalize_openapi.py failed:\n$log")
            }
            logger.lifecycle("openapi: wrote ${target.length()} bytes")
        } finally {
            tmp.delete()
        }
    }
}

abstract class CheckOpenApiDriftTask : DefaultTask() {
    @get:InputFile abstract val lockFile: RegularFileProperty
    @get:InputFile abstract val normalizerScript: RegularFileProperty
    // @Internal so Gradle doesn't infer this task must run after
    // fetchOpenApiSpec (whose output is the same file). In CI the on-disk
    // YAML is the checked-in source of truth; in dev workflow the user
    // runs regenerateOpenApi separately.
    @get:Internal abstract val onDiskYaml: RegularFileProperty

    @TaskAction
    fun run() {
        @Suppress("UNCHECKED_CAST")
        val lock = JsonSlurper().parse(lockFile.get().asFile) as Map<String, String>
        val url = "https://raw.githubusercontent.com/${lock["repo"]}/${lock["ref"]}/${lock["path"]}"
        val tmpRaw = File.createTempFile("openapi-check-", ".yaml")
        val tmpNorm = File.createTempFile("openapi-norm-", ".yaml")
        try {
            URI(url).toURL().openStream().use { input ->
                tmpRaw.outputStream().use { output -> input.copyTo(output) }
            }
            val proc = ProcessBuilder(
                "python3",
                normalizerScript.get().asFile.absolutePath,
                tmpRaw.absolutePath,
                tmpNorm.absolutePath,
            )
                .redirectErrorStream(true)
                .start()
            val log = proc.inputStream.bufferedReader().readText()
            if (proc.waitFor() != 0) {
                throw GradleException("normalize_openapi.py failed:\n$log")
            }
            if (tmpNorm.readText() != onDiskYaml.get().asFile.readText()) {
                throw GradleException(
                    "OpenAPI drift: pinned upstream YAML differs from on-disk copy. " +
                        "Run `./gradlew :core:api-generated:regenerateOpenApi` and commit the result.",
                )
            }
            logger.lifecycle("openapi: drift check OK (${lock["repo"]}@${lock["ref"]?.take(8)})")
        } finally {
            tmpRaw.delete()
            tmpNorm.delete()
        }
    }
}

abstract class PatchGeneratedTask : DefaultTask() {
    @get:InputFile abstract val patcherScript: RegularFileProperty
    @get:InputDirectory abstract val sourceTree: DirectoryProperty
    @get:OutputDirectory abstract val outputTree: DirectoryProperty

    @TaskAction
    fun run() {
        val out = outputTree.get().asFile
        out.deleteRecursively()
        out.mkdirs()
        sourceTree.get().asFile.copyRecursively(out, overwrite = true)
        val proc = ProcessBuilder(
            "python3",
            patcherScript.get().asFile.absolutePath,
            out.absolutePath,
        )
            .redirectErrorStream(true)
            .start()
        val log = proc.inputStream.bufferedReader().readText()
        if (proc.waitFor() != 0) {
            throw GradleException("patch_generated.py failed:\n$log")
        }
        logger.lifecycle(log.trim())
    }
}

val openApiLock = rootProject.file("tools/openapi.lock")
val normalizedYaml = file("src/commonMain/openapi/mobile_api.yaml")
val generatedTree = file("src/commonMain/generated")
val normalizerScriptFile = rootProject.file("tools/openapi/normalize_openapi.py")
val patcherScriptFile = rootProject.file("tools/openapi/patch_generated.py")

val fetchOpenApiSpec = tasks.register<FetchOpenApiSpecTask>("fetchOpenApiSpec") {
    group = "openapi"
    description = "Fetches mobile_api.yaml from the pinned backend SHA and normalizes it."
    lockFile.set(openApiLock)
    normalizerScript.set(normalizerScriptFile)
    outputYaml.set(normalizedYaml)
}

val checkOpenApiDrift = tasks.register<CheckOpenApiDriftTask>("checkOpenApiDrift") {
    group = "openapi"
    description = "Fails if the on-disk YAML diverges from the pinned upstream."
    lockFile.set(openApiLock)
    normalizerScript.set(normalizerScriptFile)
    onDiskYaml.set(normalizedYaml)
}

val patchGenerated = tasks.register<PatchGeneratedTask>("patchGenerated") {
    group = "openapi"
    description = "Applies post-gen patches and copies output to src/commonMain/generated."
    patcherScript.set(patcherScriptFile)
    sourceTree.set(layout.buildDirectory.dir("generated/kmpgen"))
    outputTree.set(generatedTree)
    dependsOn("kmpgenGenerateAll")
}

// The openapi-kmp-gen plugin reads the YAML at execution time but doesn't
// declare it as a Gradle input. Wire fetch -> generate so the generator sees
// the freshly-normalized YAML.
tasks.matching { it.name.startsWith("kmpgenGenerate_") }.configureEach {
    dependsOn(fetchOpenApiSpec)
}

tasks.register("regenerateOpenApi") {
    group = "openapi"
    description = "End-to-end regen: fetch + normalize + generate + patch."
    dependsOn(fetchOpenApiSpec)
    dependsOn(patchGenerated)
}

tasks.named("check") {
    dependsOn(checkOpenApiDrift)
}
