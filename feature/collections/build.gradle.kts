plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    id("bitesize.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            implementation(libs.decompose.core)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(libs.essenty.instancekeeper)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlin.logging)
        }
    }
}
