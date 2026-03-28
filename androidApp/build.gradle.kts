import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("bitesize.android.application")
}

android {
    defaultConfig {
        applicationId = "com.po4yka.bitesizereader"
        versionCode = 1
        versionName = "1.0.0"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

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
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(compose.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.startup)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.kotlin.logging)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    debugImplementation(compose.uiTooling)
}
