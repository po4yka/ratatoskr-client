package com.po4yka.ratatoskr.app

import com.arkivanov.decompose.ComponentContext
import com.po4yka.ratatoskr.feature.auth.api.AuthSessionPort
import com.po4yka.ratatoskr.navigation.AuthEntry
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import com.po4yka.ratatoskr.presentation.navigation.DefaultMainComponent
import com.po4yka.ratatoskr.presentation.navigation.DefaultRootComponent
import com.po4yka.ratatoskr.presentation.navigation.RootComponent

class AppCompositionRoot(
    private val authRepository: AuthSessionPort,
    private val authEntry: AuthEntry,
    private val mainEntries: List<MainRouteEntry>,
    private val imageUrlTransformer: (String) -> String,
) {
    fun createRoot(componentContext: ComponentContext): RootComponent =
        DefaultRootComponent(
            componentContext = componentContext,
            authRepository = authRepository,
            authEntry = authEntry,
            mainComponentFactory = { childContext ->
                DefaultMainComponent(
                    componentContext = childContext,
                    entries = mainEntries,
                )
            },
        )

    fun imageUrlTransformer(): (String) -> String = imageUrlTransformer
}
