@file:JvmName("KoinInitializerAndroid")
@file:Suppress("MatchingDeclarationName") // File contains multiple platform-specific declarations

package com.po4yka.bitesizereader.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.module

actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> = listOf(AndroidModule().module)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // Android context is provided via the appDeclaration lambda (androidContext)
}

actual fun commonModules(): List<Module> =
    listOf(
        NetworkModule().module,
        DatabaseModule().module,
        RepositoryModule().module,
        UseCaseModule().module,
        ViewModelModule().module,
    )
