package com.po4yka.bitesizereader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.po4yka.bitesizereader.di.initKoin
import com.po4yka.bitesizereader.presentation.navigation.DefaultRootComponent
import com.po4yka.bitesizereader.presentation.navigation.RootComponent

/**
 * Desktop entry point for Compose Hot Reload development
 * This allows running the app on desktop with hot reload capabilities
 */
fun main() {
    // Initialize Koin for desktop using annotation-based modules
    initKoin()

    application {
        val windowState = rememberWindowState()
        val lifecycle = LifecycleRegistry()
        val rootComponent: RootComponent =
            DefaultRootComponent(
                componentContext = DefaultComponentContext(lifecycle = lifecycle),
            )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "BiteSizeReader (Desktop Preview - Hot Reload Enabled)",
        ) {
            App(rootComponent = rootComponent)
        }
    }
}
