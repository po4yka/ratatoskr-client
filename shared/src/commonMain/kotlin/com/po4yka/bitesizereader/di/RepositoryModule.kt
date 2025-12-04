package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.repository.AuthRepositoryImpl
import com.po4yka.bitesizereader.data.repository.RequestRepositoryImpl
import com.po4yka.bitesizereader.data.repository.SearchRepositoryImpl
import com.po4yka.bitesizereader.data.repository.SummaryRepositoryImpl
import com.po4yka.bitesizereader.data.repository.SyncRepositoryImpl
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.domain.repository.RequestRepository
import com.po4yka.bitesizereader.domain.repository.SearchRepository
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::SummaryRepositoryImpl) bind SummaryRepository::class
    singleOf(::RequestRepositoryImpl) bind RequestRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class
    singleOf(::SyncRepositoryImpl) bind SyncRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}