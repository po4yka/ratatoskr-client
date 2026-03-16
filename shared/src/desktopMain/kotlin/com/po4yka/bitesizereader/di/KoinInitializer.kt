@file:JvmName("KoinInitializerDesktop")

package com.po4yka.bitesizereader.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.module

actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> = listOf(desktopPlatformModule)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // No-op for desktop
}

actual fun commonModules(): List<Module> =
    listOf(
        NetworkModule().module,
        DatabaseModule().module,
        RepositoryModule().module,
        UseCaseModule().module,
        ViewModelModule().module,
    )
