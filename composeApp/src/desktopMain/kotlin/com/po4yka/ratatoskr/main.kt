package com.po4yka.ratatoskr

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.po4yka.ratatoskr.app.assembleAppCompositionRoot
import com.po4yka.ratatoskr.di.appModules
import com.po4yka.ratatoskr.di.initKoin
import com.po4yka.ratatoskr.presentation.navigation.RootComponent

/**
 * Desktop entry point for Compose Hot Reload development
 * This allows running the app on desktop with hot reload capabilities
 */
fun main() {
    // Initialize Koin for desktop using annotation-based modules
    val koinApplication = initKoin(modules = appModules())
    val compositionRoot = assembleAppCompositionRoot(koinApplication.koin)

    application {
        val windowState = rememberWindowState()
        val lifecycle = LifecycleRegistry()
        val rootComponent: RootComponent =
            compositionRoot.createRoot(DefaultComponentContext(lifecycle = lifecycle))

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Ratatoskr (Desktop Preview - Hot Reload Enabled)",
        ) {
            App(
                rootComponent = rootComponent,
                imageUrlTransformer = compositionRoot.imageUrlTransformer(),
            )
        }
    }
}
