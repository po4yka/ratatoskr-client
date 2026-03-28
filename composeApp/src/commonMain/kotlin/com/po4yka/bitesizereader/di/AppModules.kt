package com.po4yka.bitesizereader.di

import org.koin.core.module.Module

internal expect object GeneratedAppModules {
    val coreCommon: Module
    val network: Module
    val database: Module
    val auth: Module
    val collections: Module
    val digest: Module
    val settings: Module
    val summary: Module
    val sync: Module
}

fun appModules(): List<Module> =
    listOf(
        GeneratedAppModules.coreCommon,
        GeneratedAppModules.network,
        GeneratedAppModules.database,
        GeneratedAppModules.auth,
        GeneratedAppModules.collections,
        GeneratedAppModules.digest,
        GeneratedAppModules.settings,
        GeneratedAppModules.summary,
        GeneratedAppModules.sync,
        authFeatureBindingsModule,
        collectionsFeatureBindingsModule,
        digestFeatureBindingsModule,
        settingsFeatureBindingsModule,
        summaryFeatureBindingsModule,
        syncFeatureBindingsModule,
    )
