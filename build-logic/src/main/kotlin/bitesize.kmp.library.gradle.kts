import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

extensions.configure<KotlinMultiplatformExtension> {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

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

extensions.configure<LibraryExtension> {
    namespace = buildString {
        append("com.po4yka.bitesizereader")
        project.path
            .split(':')
            .filter { it.isNotBlank() }
            .forEach { segment ->
                append('.')
                append(segment.replace("-", ""))
            }
    }
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

    tasks.configureEach {
        if (
            name != "kspCommonMainKotlinMetadata" &&
            (
                name.startsWith("compile", ignoreCase = true) ||
                    name.startsWith("ksp", ignoreCase = true)
                )
        ) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }

    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}
