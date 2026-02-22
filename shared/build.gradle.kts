import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.skie)
    alias(libs.plugins.wire)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
}

kotlin {
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

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
        languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
        languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsImplementation")
        languageSettings.enableLanguageFeature("ExpectActualClasses")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            // Export dependencies for iOS
            export(libs.decompose.core)
            export(libs.koin.core)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // AndroidX Lifecycle (Multiplatform)
            implementation(libs.androidx.lifecycle.viewmodel)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.encoding)

            // Wire gRPC
            implementation(libs.wire.runtime)
            implementation(libs.wire.grpc.client)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)

            // Decompose Navigation
            api(libs.decompose.core)
            implementation(libs.essenty.lifecycle.coroutines)

            // Koin DI
            api(libs.koin.core)
            implementation(libs.koin.annotations)

            // Kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            // Logging
            implementation(libs.kotlin.logging)
            implementation(libs.slf4j.api)

            // Multiplatform Settings
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
        }

        androidMain.dependencies {
            // Ktor Android Engine
            implementation(libs.ktor.client.okhttp)

            // SQLDelight Android Driver
            implementation(libs.sqldelight.android.driver)

            // Android Coroutines
            implementation(libs.kotlinx.coroutines.android)

            // Android DI
            implementation(libs.koin.android)

            // Android Secure Storage (Tink + DataStore)
            implementation(libs.tink.android)
            implementation(libs.datastore.preferences)

            // Logging - SLF4J backend for Android
            implementation(libs.logback.android)
        }

        iosMain.dependencies {
            // Ktor Darwin Engine
            implementation(libs.ktor.client.darwin)

            // SQLDelight Native Driver
            implementation(libs.sqldelight.native.driver)
        }

        named("desktopMain").dependencies {
            // Ktor OkHttp Engine (same as Android)
            implementation(libs.ktor.client.okhttp)

            // SQLDelight SQLite JDBC Driver for Desktop
            implementation(libs.sqldelight.sqlite.driver)

            // Logging - SLF4J backend for Desktop (logback-classic, not Android variant)
            implementation(libs.logback.classic)

            // MapSettings for in-memory storage (dev only, no persistence needed)
            implementation(libs.multiplatform.settings.test)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.turbine)
            implementation(libs.multiplatform.settings.test)
            // MockK only supports JVM, moved to androidUnitTest
            // implementation(libs.mockk)
        }

        androidUnitTest.dependencies {
            implementation(libs.mockk)
        }
    }
}

android {
    namespace = "com.po4yka.bitesizereader.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.po4yka.bitesizereader.database")
            srcDirs.setFrom("src/commonMain/sqldelight")
        }
    }
}

wire {
    kotlin {
        // Use explicit streaming call classes (GrpcServerStreamingCall, etc.)
        // instead of deprecated GrpcStreamingCall for non-bidirectional streaming
        explicitStreamingCalls = true
    }
    sourcePath {
        srcDir("src/commonMain/proto")
    }
}

// SKIE configuration for better Swift/Kotlin interop
skie {
    // Temporarily disabled because the configured SKIE version doesn't support Kotlin 2.3.x yet.
    // Re-enable after upgrading SKIE to a version that supports the Kotlin version in use.
    isEnabled = false

    features {
        enableSwiftUIObservingPreview = true
    }
}

// KSP configuration for Koin Annotations
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
    add("kspDesktop", libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "false")
    arg("KOIN_DEFAULT_MODULE", "false")
}

// Required for KMP: Make KSP-generated code visible to common source sets
// See: https://insert-koin.io/docs/reference/koin-annotations/kmp/
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

// KSP platform tasks also need to depend on common metadata KSP
tasks.withType<com.google.devtools.ksp.gradle.KspAATask>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}
