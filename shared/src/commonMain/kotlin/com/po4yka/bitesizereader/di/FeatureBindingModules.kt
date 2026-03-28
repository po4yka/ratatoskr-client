package com.po4yka.bitesizereader.di

import org.koin.core.module.Module

internal fun featureBindingModules(): List<Module> =
    listOf(
        authFeatureBindingsModule,
        collectionsFeatureBindingsModule,
        digestFeatureBindingsModule,
        settingsFeatureBindingsModule,
        summaryFeatureBindingsModule,
    )
