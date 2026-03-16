package com.po4yka.bitesizereader.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.module

/**
 * iOS platform configuration placeholder.
 */
actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> = listOf(iosPlatformModule)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // No-op for iOS
}

actual fun commonModules(): List<Module> =
    listOf(
        NetworkModule().module,
        DatabaseModule().module,
        RepositoryModule().module,
        UseCaseModule().module,
        ViewModelModule().module,
    )
