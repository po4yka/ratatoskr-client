import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("bitesize.android.application")
}

val debugApiBaseUrl = "http://10.0.2.2:8000"
val productionApiBaseUrl = "https://bitsizereaderapi.po4yka.com"

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun buildConfigString(value: String): String = "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

fun localProperty(
    name: String,
    defaultValue: String,
): String = localProperties.getProperty(name, defaultValue).trim()

fun releaseApiBaseUrl(): String {
    val explicitReleaseUrl = localProperties.getProperty("api.release.base.url")?.trim()
    val httpsBaseUrl =
        localProperties
            .getProperty("api.base.url")
            ?.trim()
            ?.takeIf { it.startsWith("https://", ignoreCase = true) }

    return explicitReleaseUrl ?: httpsBaseUrl ?: productionApiBaseUrl
}

android {
    defaultConfig {
        applicationId = "com.po4yka.bitesizereader"
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField(
            "String",
            "CLIENT_ID",
            buildConfigString(localProperty("client.id", "android-app-v1.0")),
        )
        buildConfigField(
            "int",
            "API_TIMEOUT_SECONDS",
            localProperty("api.timeout.seconds", "30"),
        )
        buildConfigField(
            "boolean",
            "API_LOGGING_ENABLED",
            "false",
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
            buildConfigField(
                "String",
                "API_BASE_URL",
                buildConfigString(localProperty("api.base.url", debugApiBaseUrl)),
            )
            buildConfigField(
                "boolean",
                "API_LOGGING_ENABLED",
                localProperty("api.logging.enabled", "false"),
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField(
                "String",
                "API_BASE_URL",
                buildConfigString(releaseApiBaseUrl()),
            )
            buildConfigField("boolean", "API_LOGGING_ENABLED", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

gradle.taskGraph.whenReady {
    val isReleaseTaskRequested =
        allTasks.any { task ->
            task.project == project && task.name.contains("Release")
        }
    if (isReleaseTaskRequested) {
        require(releaseApiBaseUrl().startsWith("https://", ignoreCase = true)) {
            "Release API_BASE_URL must use HTTPS. Set api.release.base.url to an https:// URL " +
                "or remove it to use $productionApiBaseUrl."
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
