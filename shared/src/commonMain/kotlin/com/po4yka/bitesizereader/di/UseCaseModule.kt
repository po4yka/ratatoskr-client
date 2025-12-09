package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.domain.usecase.CreateInviteLinkUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteCollectionUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteSummaryUseCase
import com.po4yka.bitesizereader.domain.usecase.DownloadDatabaseUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCollectionAclUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCollectionItemsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCollectionUseCase
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
import com.po4yka.bitesizereader.domain.usecase.ManageCollaboratorUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.RetryRequestUseCase
import com.po4yka.bitesizereader.domain.usecase.SearchSummariesUseCase
import com.po4yka.bitesizereader.domain.usecase.SubmitURLUseCase
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.domain.usecase.UnlinkTelegramUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateCollectionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule =
    module {
        factoryOf(::GetSummariesUseCase)
        factoryOf(::GetSummaryByIdUseCase)
        factoryOf(::MarkSummaryAsReadUseCase)
        factoryOf(::DeleteSummaryUseCase)
        factoryOf(::SubmitURLUseCase)
        factoryOf(::GetRequestStatusUseCase)
        factoryOf(::RetryRequestUseCase)
        factoryOf(::SearchSummariesUseCase)
        factoryOf(::DownloadDatabaseUseCase)
        factoryOf(::GetTelegramLinkStatusUseCase)
        factoryOf(::UnlinkTelegramUseCase)
        factoryOf(::LinkTelegramUseCase)
        factoryOf(::LoginWithSecretUseCase)
        factoryOf(::LoginWithTelegramUseCase)
        factoryOf(::LogoutUseCase)
        factoryOf(::GetCurrentUserUseCase)
        factoryOf(::GetTrendingTopicsUseCase)
        factoryOf(::SyncDataUseCase)
        // Collection use cases
        factoryOf(::GetCollectionUseCase)
        factoryOf(::GetCollectionItemsUseCase)
        factoryOf(::UpdateCollectionUseCase)
        factoryOf(::DeleteCollectionUseCase)
        factoryOf(::GetCollectionAclUseCase)
        factoryOf(::ManageCollaboratorUseCase)
        factoryOf(::CreateInviteLinkUseCase)
    }
