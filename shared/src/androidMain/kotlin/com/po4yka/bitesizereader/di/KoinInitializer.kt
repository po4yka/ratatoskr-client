@file:JvmName("KoinInitializerAndroid")

package com.po4yka.bitesizereader.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.module

actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> =
    listOf(AndroidPlatformModule().module)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // Android context is provided via the appDeclaration lambda (androidContext)
    platformContext = koin.get()
}
