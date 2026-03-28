package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.navigation.AuthEntry
import com.po4yka.bitesizereader.navigation.RootChildDescriptor
import com.po4yka.bitesizereader.navigation.RootScreen
import com.po4yka.bitesizereader.presentation.navigation.DefaultAuthComponent
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
        single<AuthEntry> {
            val koin = getKoin()
            AuthEntry { componentContext, onLoginSuccess ->
                RootChildDescriptor(
                    screen = RootScreen.AUTH,
                    component =
                        DefaultAuthComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<AuthViewModel>() },
                            onLoginSuccessCallback = onLoginSuccess,
                        ),
                )
            }
        }
    }
