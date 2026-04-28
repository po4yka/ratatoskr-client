package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.util.config.AppConfig
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Platform configuration required for dependency injection.
 */
expect class PlatformConfiguration()

/**
 * Platform-specific modules required for DI (KSP-generated).
 */
expect fun platformModules(configuration: PlatformConfiguration): List<Module>

/**
 * Apply platform-specific configuration to the Koin application (e.g., Android context).
 */
expect fun KoinApplication.platformExtras(configuration: PlatformConfiguration)

/**
 * Initialize Koin with shared modules and platform-specific bindings.
 */
fun initKoin(
    configuration: PlatformConfiguration = PlatformConfiguration(),
    modules: List<Module>,
    appDeclaration: KoinAppDeclaration = {},
    extraModules: List<Module> = emptyList(),
): KoinApplication =
    startKoin {
        setupKoin(configuration = configuration, modules = modules, extraModules = extraModules)
        appDeclaration()
    }

/**
 * Setup Koin configuration without starting it.
 * Useful for platform-specific startup integration (e.g. AndroidX Startup).
 */
fun KoinApplication.setupKoin(
    configuration: PlatformConfiguration = PlatformConfiguration(),
    modules: List<Module>,
    extraModules: List<Module> = emptyList(),
) {
    platformExtras(configuration)
    modules(platformModules(configuration) + modules + extraModules)
    properties(
        mapOf(
            "api.base.url" to AppConfig.Api.baseUrl,
            "api.logging.enabled" to AppConfig.Api.loggingEnabled.toString(),
            "telegram.bot.username" to AppConfig.Telegram.botUsername,
            "telegram.bot.id" to AppConfig.Telegram.botId,
        ),
    )
}
