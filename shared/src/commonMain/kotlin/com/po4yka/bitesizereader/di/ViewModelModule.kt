package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule = module {
    factoryOf(::SummaryListViewModel)
    factoryOf(::SummaryDetailViewModel)
    factoryOf(::SubmitURLViewModel)
    factoryOf(::SearchViewModel)
    factoryOf(::AuthViewModel)
}
