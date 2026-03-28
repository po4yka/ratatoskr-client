package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import org.koin.dsl.module

// ViewModels are wired manually to avoid duplicate BaseViewModel KSP symbols in native frameworks.
val authFeatureBindingsModule =
    module {
        single {
            AuthViewModel(
                loginWithTelegramUseCase = get(),
                loginWithSecretUseCase = get(),
                logoutUseCase = get(),
                getCurrentUserUseCase = get(),
                getDeveloperCredentialsUseCase = get(),
                saveDeveloperCredentialsUseCase = get(),
                clearDeveloperCredentialsUseCase = get(),
            )
        }
    }
