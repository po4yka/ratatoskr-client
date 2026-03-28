plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    id("bitesize.kmp.library")
    kotlin("native.cocoapods")
}

kotlin {
    cocoapods {
        name = "ComposeApp"
        summary = "Compose Multiplatform UI for Bite-Size Reader"
        homepage = "https://github.com/po4yka/bite-size-reader-client"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
            export(projects.core.common)
            export(projects.core.data)
            export(projects.core.navigation)
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
            api(projects.core.common)
            api(projects.core.data)
            api(projects.core.navigation)
            api(projects.feature.auth)
            api(projects.feature.collections)
            api(projects.feature.digest)
            api(projects.feature.settings)
            api(projects.feature.summary)
            api(projects.feature.sync)
            implementation(projects.core.ui)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.koin.compose)
            implementation(libs.kotlin.logging)
            implementation(libs.decompose.core)
            implementation(libs.decompose.compose)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.carbon.compose)
            implementation(libs.markdown.renderer)
            implementation(libs.markdown.renderer.m3)
            implementation(libs.markdown.renderer.coil3)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.startup)
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.mockk)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
