package com.po4yka.bitesizereader.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        networkModule,
        databaseModule,
        repositoryModule,
        useCaseModule,
        viewModelModule,
        platformModule()
    )
}

expect fun platformModule(): Module
