package com.po4yka.bitesizereader.app

import com.arkivanov.decompose.ComponentContext
import com.po4yka.bitesizereader.domain.port.AuthSessionPort
import com.po4yka.bitesizereader.domain.usecase.GetProxiedImageUrlUseCase
import com.po4yka.bitesizereader.navigation.AuthEntry
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.presentation.navigation.DefaultMainComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultRootComponent
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import org.koin.core.Koin

class AppCompositionRoot(
    internal val koin: Koin,
) {
    fun createRoot(componentContext: ComponentContext): RootComponent =
        DefaultRootComponent(
            componentContext = componentContext,
            authRepository = koin.get<AuthSessionPort>(),
            authEntry = koin.get<AuthEntry>(),
            mainComponentFactory = { childContext ->
                DefaultMainComponent(
                    componentContext = childContext,
                    entries = koin.getAll<MainRouteEntry>(),
                )
            },
        )

    fun imageUrlTransformer(): (String) -> String = koin.get<GetProxiedImageUrlUseCase>()::invoke
}
