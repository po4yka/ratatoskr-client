package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.navigation.AuthEntry
import com.po4yka.ratatoskr.navigation.RootChildDescriptor
import com.po4yka.ratatoskr.navigation.RootScreen
import com.po4yka.ratatoskr.feature.auth.ui.screens.AuthScreen
import com.po4yka.ratatoskr.presentation.navigation.DefaultAuthComponent
import com.po4yka.ratatoskr.presentation.viewmodel.AuthViewModel
import org.koin.core.Koin
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

fun authEntry(koin: Koin): AuthEntry =
    AuthEntry { componentContext, onLoginSuccess ->
        val authComponent =
            DefaultAuthComponent(
                componentContext = componentContext,
                viewModelFactory = { koin.get<AuthViewModel>() },
                onLoginSuccessCallback = onLoginSuccess,
            )
        RootChildDescriptor(
            screen = RootScreen.AUTH,
            component = authComponent,
            render = { AuthScreen(component = authComponent) },
        )
    }
