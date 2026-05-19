import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(projects.shared.sharedUI)
    implementation(projects.shared.sharedLogic)
    implementation(compose.desktop.currentOs)
    implementation(libs.decompose.core)
    implementation(libs.essenty.lifecycle.coroutines)
    implementation(libs.kotlin.logging)
}

compose.desktop {
    application {
        mainClass = "com.po4yka.ratatoskr.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Ratatoskr"
            packageVersion = "1.0.0"
        }
    }
}
