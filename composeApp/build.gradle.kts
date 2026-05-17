plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    id("ratatoskr.kmp.library")
    kotlin("native.cocoapods")
}

kotlin {
    cocoapods {
        name = "ComposeApp"
        summary = "Compose Multiplatform UI for Ratatoskr Client"
        homepage = "https://github.com/po4yka/ratatoskr-client"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
            // ObjC export surface — keep this minimal. A module needs an
            // `export()` line ONLY when its types appear in the public
            // Swift-visible API of `ComposeApp.framework` (i.e. on a
            // public Kotlin symbol used directly from Swift). Internal
            // deps stay as `api(...)` below so they compile but do not
            // bloat the ObjC headers or the framework binary.
            //
            // Swift consumers (`iosApp/iosApp/iOSApp.swift`,
            // `ContentView.swift`) only touch `IosAppHost`,
            // `IosKoinBootstrap`, `ComposeRootViewControllerFactory`,
            // and `RootComponent`. Of those:
            //   - IosAppHost / IosKoinBootstrap / Factory live in this
            //     module → no export needed for the symbol itself.
            //   - RootComponent uses ChildStack/Value (Decompose) and
            //     RootChildDescriptor/AppRoute (core/navigation) on its
            //     public surface, so those modules MUST be exported.
            //   - core/common is a transitive contract used by
            //     core/navigation types and shared models — exported.
            //
            // core/data, core/api-generated, every feature/*, and
            // koin.core are NOT referenced from Swift and are not
            // surfaced through any exported Kotlin symbol, so they are
            // intentionally omitted. ShareExtension and
            // RecentSummariesWidget do not import ComposeApp at all
            // (verified 2026-05-17).
            export(projects.core.common)
            export(projects.core.navigation)
            export(libs.decompose.core)
        }
    }

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
            implementation(projects.core.ui)

            implementation(compose.runtime)
            implementation(compose.foundation)
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
            implementation(libs.markdown.renderer)
            implementation(libs.markdown.renderer.coil3)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.startup)
        }

        androidHostTest.dependencies {
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
