package com.po4yka.ratatoskr.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module

/**
 * iOS platform configuration placeholder.
 */
actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> = listOf(iosPlatformModule)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // No-op for iOS
}
