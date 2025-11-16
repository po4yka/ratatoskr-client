package com.po4yka.bitesizereader.di

import org.koin.core.module.Module

/**
 * All application modules combined
 */
fun appModules(): List<Module> = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule
)
