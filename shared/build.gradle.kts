import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
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
            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.encoding)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)

            // Decompose Navigation
            api(libs.decompose.core)

            // Store (Repository Pattern)
            implementation(libs.store)

            // Koin DI
            api(libs.koin.core)

            // Kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // Logging
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            // Ktor Android Engine
            implementation(libs.ktor.client.okhttp)

            // SQLDelight Android Driver
            implementation(libs.sqldelight.android.driver)

            // Android Coroutines
            implementation(libs.kotlinx.coroutines.android)

            // Android Security
            implementation(libs.androidx.security.crypto)
        }

        iosMain.dependencies {
            // Ktor Darwin Engine
            implementation(libs.ktor.client.darwin)

            // SQLDelight Native Driver
            implementation(libs.sqldelight.native.driver)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.turbine)
        }
    }
}

android {
    namespace = "com.po4yka.bitesizereader.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

// SKIE configuration for better Swift/Kotlin interop
skie {
    features {
        enableSwiftUIObservingPreview = true
    }
}
