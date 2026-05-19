plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    id("ratatoskr.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.apiGenerated)
            api(projects.core.common)
            api(projects.core.data)
            api(projects.core.navigation)
            api(projects.feature.auth)
            api(projects.feature.collections)
            api(projects.feature.digest)
            api(projects.feature.settings)
            api(projects.feature.summary)
            api(projects.feature.sync)

            implementation(libs.koin.core)
            implementation(libs.kotlin.logging)
            implementation(libs.decompose.core)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.startup)
        }

        androidHostTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.mockk)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
