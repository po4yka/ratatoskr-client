plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    id("ratatoskr.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(projects.core.data)
            implementation(libs.ktor.client.core)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlin.logging)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.serialization)
        }
    }
}
