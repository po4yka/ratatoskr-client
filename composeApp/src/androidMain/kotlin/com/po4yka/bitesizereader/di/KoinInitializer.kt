@file:JvmName("KoinInitializerAndroid")
@file:Suppress("MatchingDeclarationName") // File contains multiple platform-specific declarations

package com.po4yka.bitesizereader.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_AuthFeatureModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_CollectionsFeatureModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_CoreCommonModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_DatabaseModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_DigestFeatureModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_NetworkModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_SettingsFeatureModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_SummaryFeatureModule
import org.koin.ksp.generated.com_po4yka_bitesizereader_di_SyncFeatureModule
import org.koin.ksp.generated.module

actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> = listOf(AndroidModule().module)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // Android context is provided via the appDeclaration lambda (androidContext)
}

actual fun appModules(): List<Module> =
    listOf(
        com_po4yka_bitesizereader_di_CoreCommonModule,
        com_po4yka_bitesizereader_di_NetworkModule,
        com_po4yka_bitesizereader_di_DatabaseModule,
        com_po4yka_bitesizereader_di_AuthFeatureModule,
        com_po4yka_bitesizereader_di_CollectionsFeatureModule,
        com_po4yka_bitesizereader_di_DigestFeatureModule,
        com_po4yka_bitesizereader_di_SettingsFeatureModule,
        com_po4yka_bitesizereader_di_SummaryFeatureModule,
        com_po4yka_bitesizereader_di_SyncFeatureModule,
        authFeatureBindingsModule,
        collectionsFeatureBindingsModule,
        digestFeatureBindingsModule,
        settingsFeatureBindingsModule,
        summaryFeatureBindingsModule,
        syncFeatureBindingsModule,
    )
