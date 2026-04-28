plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    id("ratatoskr.kmp.library")
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.essenty.instancekeeper)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlin.logging)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.turbine)
            implementation(libs.multiplatform.settings.test)
        }
    }
}
