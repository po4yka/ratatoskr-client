package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.po4yka.bitesizereader.feature.auth.api.AuthSessionPort
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.AuthEntry
import com.po4yka.bitesizereader.navigation.RootChildDescriptor
import com.po4yka.bitesizereader.navigation.RootScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.po4yka.bitesizereader.ui.screens.MainScreen

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val authRepository: AuthSessionPort,
    private val authEntry: AuthEntry,
    private val mainComponentFactory: (ComponentContext) -> MainComponent,
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    // Use lifecycle-bound coroutine scope from Decompose/Essenty
    // Must use Main dispatcher for navigation operations
    private val scope = coroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val childStack: Value<ChildStack<*, RootChildDescriptor>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Auth,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    init {
        initializeAuthState()
        observeAuthState()
    }

    private fun initializeAuthState() {
        scope.launch {
            authRepository.checkAuthStatus()
        }
    }

    private fun observeAuthState() {
        authRepository.isAuthenticated
            .onEach { isAuthenticated ->
                val currentConfig = childStack.value.active.configuration
                if (isAuthenticated && currentConfig is Config.Auth) {
                    navigation.replaceCurrent(Config.Main)
                } else if (!isAuthenticated && currentConfig is Config.Main) {
                    navigation.replaceCurrent(Config.Auth)
                }
            }
            .launchIn(scope)
    }

    override fun open(route: AppRoute) {
        if (childStack.value.active.configuration is Config.Auth) {
            navigation.replaceCurrent(Config.Main)
        }
        activeMainComponent()?.open(route)
    }

    private fun activeMainComponent(): MainComponent? = childStack.value.active.instance.component as? MainComponent

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootChildDescriptor =
        when (config) {
            is Config.Auth ->
                authEntry.create(componentContext) {
                    navigation.replaceCurrent(Config.Main)
                }

            is Config.Main ->
                mainComponentFactory(componentContext).let { mainComponent ->
                    RootChildDescriptor(
                        screen = RootScreen.MAIN,
                        component = mainComponent,
                        render = { MainScreen(component = mainComponent) },
                    )
                }
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Auth : Config

        @Serializable
        data object Main : Config
    }
}
