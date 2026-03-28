plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    id("bitesize.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.decompose.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
