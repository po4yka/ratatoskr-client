package com.po4yka.bitesizereader.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initialize Koin for iOS
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            iosModule,
            networkModule,
            databaseModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
        properties(
            mapOf(
                "api.base.url" to "https://api.bitesizereader.example.com", // TODO: Update with actual API URL
                "api.logging.enabled" to "true"
            )
        )
    }
