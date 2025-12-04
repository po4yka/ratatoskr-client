package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.domain.usecase.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetSummariesUseCase)
    factoryOf(::GetSummaryByIdUseCase)
    factoryOf(::MarkSummaryAsReadUseCase)
    factoryOf(::DeleteSummaryUseCase)
    factoryOf(::SubmitURLUseCase)
    factoryOf(::GetRequestStatusUseCase)
    factoryOf(::RetryRequestUseCase)
    factoryOf(::SearchSummariesUseCase)
    factoryOf(::GetTrendingTopicsUseCase)
    factoryOf(::SyncDataUseCase)
    factoryOf(::LoginWithTelegramUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::GetCurrentUserUseCase)
}