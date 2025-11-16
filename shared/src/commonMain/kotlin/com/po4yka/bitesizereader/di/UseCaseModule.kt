package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.domain.usecase.*
import org.koin.dsl.module

/**
 * Koin module for use case dependencies
 */
val useCaseModule = module {
    factory { GetSummariesUseCase(get()) }
    factory { GetSummaryByIdUseCase(get()) }
    factory { SubmitURLUseCase(get()) }
    factory { LoginWithTelegramUseCase(get()) }
    factory { SyncDataUseCase(get()) }
    factory { SearchSummariesUseCase(get()) }
    factory { MarkSummaryAsReadUseCase(get()) }
}
