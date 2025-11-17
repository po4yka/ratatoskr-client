package com.po4yka.bitesizereader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.po4yka.bitesizereader.di.*
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.util.config.AppConfig
import org.koin.core.context.startKoin

/**
 * Desktop entry point for Compose Hot Reload development
 * This allows running the app on desktop with hot reload capabilities
 */
fun main() {
    // Initialize Koin for desktop
    startKoin {
        modules(
            desktopModule,
            networkModule,
            databaseModule,
            repositoryModule,
            useCaseModule,
            viewModelModule,
        )
        properties(
            mapOf(
                "api.base.url" to AppConfig.Api.baseUrl,
                "api.logging.enabled" to AppConfig.Api.loggingEnabled.toString(),
                "telegram.bot.username" to AppConfig.Telegram.botUsername,
                "telegram.bot.id" to AppConfig.Telegram.botId,
            ),
        )
    }

    application {
        val windowState = rememberWindowState()
        val lifecycle = LifecycleRegistry()
        val rootComponent =
            RootComponent(
                componentContext = DefaultComponentContext(lifecycle = lifecycle),
            )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "BiteSizeReader (Desktop Preview - Hot Reload Enabled)",
        ) {
            App(rootComponent)
        }
    }
}
