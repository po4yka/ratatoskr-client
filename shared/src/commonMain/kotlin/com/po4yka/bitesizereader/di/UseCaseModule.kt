package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCurrentUserUseCase
import com.po4yka.bitesizereader.domain.usecase.GetRequestStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.GetSummaryByIdUseCase
import com.po4yka.bitesizereader.domain.usecase.GetTelegramLinkStatusUseCase
import com.po4yka.bitesizereader.domain.usecase.GetTrendingTopicsUseCase
import com.po4yka.bitesizereader.domain.usecase.LinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithSecretUseCase
import com.po4yka.bitesizereader.domain.usecase.LoginWithTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.LogoutUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.RetryRequestUseCase
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.SubmitURLUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.domain.usecase.UnlinkTelegramUseCase
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
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
    factoryOf(::LoginWithSecretUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::GetTelegramLinkStatusUseCase)
    factoryOf(::UnlinkTelegramUseCase)
    factoryOf(::LinkTelegramUseCase)
    factoryOf(::SettingsViewModel)
}
