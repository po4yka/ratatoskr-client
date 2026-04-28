plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    id("ratatoskr.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.decompose.core)
            implementation(compose.runtime)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
