package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.*
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * ViewModel module
 *
 * All ViewModels extend BaseViewModel which provides lifecycle-managed CoroutineScope.
 * No need to inject CoroutineScope anymore.
 */
val viewModelModule =
    module {
        factory { LoginViewModel(get(), get()) }
        factory { SummaryListViewModel(get(), get(), get()) }
        factory { (summaryId: Int) ->
            SummaryDetailViewModel(summaryId, get(), get())
        }
        factory { SubmitURLViewModel(get(), get()) }
        factory { SearchViewModel(get(), get()) }
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
    )
