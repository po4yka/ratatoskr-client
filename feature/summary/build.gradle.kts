plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    id("ratatoskr.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.navigation)
            implementation(projects.core.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(projects.feature.auth)
            implementation(projects.feature.collections)
            implementation(projects.feature.sync)
            implementation(libs.decompose.core)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(libs.essenty.instancekeeper)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.markdown.renderer)
            implementation(libs.markdown.renderer.coil3)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.ktor.client.core)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlin.logging)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
