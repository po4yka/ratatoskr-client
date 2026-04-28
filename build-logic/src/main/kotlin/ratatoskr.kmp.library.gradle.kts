import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins.withId("com.android.kotlin.multiplatform.library") {
    val kmp = extensions.getByType(KotlinMultiplatformExtension::class.java)
    val androidLibrary = (kmp as ExtensionAware)
        .extensions
        .getByType(KotlinMultiplatformAndroidLibraryExtension::class.java)

    androidLibrary.namespace = buildString {
        append("com.po4yka.ratatoskr")
        project.path
            .split(':')
            .filter { it.isNotBlank() }
            .forEach { segment ->
                append('.')
                append(segment.replace("-", ""))
            }
    }
    androidLibrary.compileSdk = 36
    androidLibrary.minSdk = 24
    androidLibrary.withHostTestBuilder { }

    tasks.withType(KotlinJvmCompile::class.java).configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

extensions.configure<KotlinMultiplatformExtension> {
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
        languageSettings.enableLanguageFeature("ExpectActualClasses")
    }
}

pluginManager.withPlugin("com.google.devtools.ksp") {
    dependencies {
        add("kspCommonMainMetadata", "io.insert-koin:koin-ksp-compiler:2.3.1")
        add("kspAndroid", "io.insert-koin:koin-ksp-compiler:2.3.1")
        add("kspIosX64", "io.insert-koin:koin-ksp-compiler:2.3.1")
        add("kspIosArm64", "io.insert-koin:koin-ksp-compiler:2.3.1")
        add("kspIosSimulatorArm64", "io.insert-koin:koin-ksp-compiler:2.3.1")
        add("kspDesktop", "io.insert-koin:koin-ksp-compiler:2.3.1")
    }

    extensions.findByName("ksp")?.let { extension ->
        extension.javaClass.methods
            .firstOrNull { it.name == "arg" && it.parameterCount == 2 }
            ?.let { argMethod ->
                argMethod.invoke(extension, "KOIN_CONFIG_CHECK", "false")
                argMethod.invoke(extension, "KOIN_DEFAULT_MODULE", "false")
            }
    }

    tasks.matching { it.name == "compileCommonMainKotlinMetadata" }.configureEach {
        dependsOn("kspCommonMainKotlinMetadata")
    }

    tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
        dependsOn("kspCommonMainKotlinMetadata")
    }

    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}
