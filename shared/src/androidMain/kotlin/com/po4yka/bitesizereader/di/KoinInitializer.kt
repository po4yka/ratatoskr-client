@file:JvmName("KoinInitializerAndroid")

package com.po4yka.bitesizereader.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module

actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> =
    listOf(platformModule())

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // Android context is provided via the appDeclaration lambda (androidContext)
}
