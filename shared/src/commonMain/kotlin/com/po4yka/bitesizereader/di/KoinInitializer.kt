package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.util.config.AppConfig
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module

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
 * Common modules using KSP-generated modules.
 */
fun commonModules(): List<Module> =
    listOf(
        NetworkModule().module,
        DatabaseModule().module,
        RepositoryModule().module,
        UseCaseModule().module,
        ViewModelModule().module,
        CoroutineScopeModule().module,
    )

/**
 * Initialize Koin with shared modules and platform-specific bindings.
 */
fun initKoin(
    configuration: PlatformConfiguration = PlatformConfiguration(),
    appDeclaration: KoinAppDeclaration = {},
    extraModules: List<Module> = emptyList(),
): KoinApplication =
    startKoin {
        setupKoin(configuration, extraModules)
        appDeclaration()
    }

/**
 * Setup Koin configuration without starting it.
 * Useful for platform-specific startup integration (e.g. AndroidX Startup).
 */
fun KoinApplication.setupKoin(
    configuration: PlatformConfiguration = PlatformConfiguration(),
    extraModules: List<Module> = emptyList(),
) {
    platformExtras(configuration)
    modules(platformModules(configuration) + commonModules() + extraModules)
    properties(
        mapOf(
            "api.base.url" to AppConfig.Api.baseUrl,
            "api.logging.enabled" to AppConfig.Api.loggingEnabled.toString(),
            "telegram.bot.username" to AppConfig.Telegram.botUsername,
            "telegram.bot.id" to AppConfig.Telegram.botId,
        ),
    )
}
