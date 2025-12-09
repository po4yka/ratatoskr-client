package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.*
import com.po4yka.bitesizereader.util.FileSaver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Coroutine Scope module
 */
val coroutineScopeModule =
    module {
        single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    }

val utilityModule =
    module {
        // FileSaver is platform specific, so it should be provided by platformModule
        // but if we want to use factoryOf in common code, we need to expect it or similar.
        // simpler: expect/actual class FileSaver allows direct usage or definition in platform module.
        // Check Koin.kt to see how platformModule is defined.
    }

/**
 * ViewModel module
 */
val viewModelModule =
    module {
        factory { SummaryListViewModel(get(), get(), get()) }
        factory { (summaryId: String) -> SummaryDetailViewModel(get(), get(), get()) }
        factory { SubmitURLViewModel(get()) }
        factory { SearchViewModel(get(), get()) }
        single { AuthViewModel(get(), get(), get(), get()) }
        factory { SettingsViewModel(get(), get(), get(), get()) }
    }

/**
 * All application modules combined
 */
fun appModules(): List<Module> =
    listOf(
        networkModule,
        databaseModule,
        repositoryModule,
        useCaseModule,
        viewModelModule,
        coroutineScopeModule,
    )
