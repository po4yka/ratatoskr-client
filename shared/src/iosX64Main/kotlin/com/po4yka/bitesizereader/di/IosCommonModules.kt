package com.po4yka.bitesizereader.di

import org.koin.core.module.Module
import org.koin.ksp.generated.module

actual fun commonModules(): List<Module> =
    listOf(
        NetworkModule().module,
        DatabaseModule().module,
        RepositoryModule().module,
        UseCaseModule().module,
        ViewModelModule().module,
    )
