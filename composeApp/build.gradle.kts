import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain.dependencies {
            // Compose
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Koin for Android
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // Decompose for Compose
            implementation(libs.decompose.compose)

            // Coil for image loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // WorkManager for background sync
            implementation(libs.androidx.work.runtime)

            // Coroutines
            implementation(libs.kotlinx.coroutines.android)
        }

        commonMain.dependencies {
            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Shared module
            implementation(projects.shared)

            // Koin
            implementation(libs.koin.core)

            // Decompose
            implementation(libs.decompose.core)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.decompose.compose)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.po4yka.bitesizereader"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.po4yka.bitesizereader"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        // Load configuration from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

        // BuildConfig fields
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${localProperties.getProperty("api.base.url", "http://10.0.2.2:8000")}\"",
        )
        buildConfigField(
            "String",
            "CLIENT_ID",
            "\"${localProperties.getProperty("client.id", "android-app-v1.0")}\"",
        )
        buildConfigField(
            "int",
            "API_TIMEOUT_SECONDS",
            localProperties.getProperty("api.timeout.seconds", "30"),
        )
        buildConfigField(
            "boolean",
            "API_LOGGING_ENABLED",
            localProperties.getProperty("api.logging.enabled", "true"),
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
