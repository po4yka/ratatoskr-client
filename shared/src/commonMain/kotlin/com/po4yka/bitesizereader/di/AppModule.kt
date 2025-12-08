package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Coroutine Scope module
 */
val coroutineScopeModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}

/**
 * ViewModel module
 */
val viewModelModule = module {
    factory { SummaryListViewModel(get(), get()) }
    factory { (summaryId: String) -> SummaryDetailViewModel(get(), get(), get()) }
    factory { SubmitURLViewModel(get(), get()) }
    factory { SearchViewModel(get(), get()) }
    single { AuthViewModel(get(), get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get(), get()) }
}

/**
 * All application modules combined
 */
fun appModules(): List<Module> = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
    coroutineScopeModule
)
