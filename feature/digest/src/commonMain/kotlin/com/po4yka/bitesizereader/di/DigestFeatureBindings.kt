package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestCreateViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.DigestViewModel
import org.koin.dsl.module

val digestFeatureBindingsModule =
    module {
        factory {
            DigestViewModel(
                getDigestChannelsUseCase = get(),
                manageDigestSubscriptionUseCase = get(),
                getDigestPreferencesUseCase = get(),
                updateDigestPreferencesUseCase = get(),
                getDigestHistoryUseCase = get(),
                triggerDigestUseCase = get(),
            )
        }
        factory {
            CustomDigestCreateViewModel(
                getSummariesUseCase = get(),
                createCustomDigestUseCase = get(),
            )
        }
        factory {
            CustomDigestViewViewModel(
                getCustomDigestByIdUseCase = get(),
                deleteCustomDigestUseCase = get(),
                repository = get(),
            )
        }
    }
