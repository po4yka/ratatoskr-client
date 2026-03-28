package com.po4yka.bitesizereader.di

import org.koin.core.module.Module
import org.koin.ksp.generated.module

actual fun appModules(): List<Module> =
    listOf(
        NetworkModule().module,
        DatabaseModule().module,
        AuthFeatureModule().module,
        CollectionsFeatureModule().module,
        DigestFeatureModule().module,
        SettingsFeatureModule().module,
        SummaryFeatureModule().module,
        SyncFeatureModule().module,
    ) + featureBindingModules()
