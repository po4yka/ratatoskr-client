import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    id("bitesize.kmp.library")
    alias(libs.plugins.skie)
    alias(libs.plugins.kover)
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.core)
            export(projects.feature.auth)
            export(projects.feature.collections)
            export(projects.feature.digest)
            export(projects.feature.settings)
            export(projects.feature.summary)
            export(projects.feature.sync)
            export(libs.decompose.core)
            export(libs.koin.core)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            api(projects.feature.auth)
            api(projects.feature.collections)
            api(projects.feature.digest)
            api(projects.feature.settings)
            api(projects.feature.summary)
            api(projects.feature.sync)
            api(libs.decompose.core)
            implementation(libs.essenty.lifecycle.coroutines)
            api(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlin.logging)
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

        androidUnitTest.dependencies {
            implementation(libs.mockk)
        }
    }
}

skie {
    isEnabled = false
    features {
        enableSwiftUIObservingPreview = true
    }
}
