package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {
    private val navigationRegistry = DefaultNavigationRegistry.fromKoin()
    private val navigation = StackNavigation<Config>()
    private val authRepository: AuthRepository = navigationRegistry.authRepository

    // Use lifecycle-bound coroutine scope from Decompose/Essenty
    // Must use Main dispatcher for navigation operations
    private val scope = coroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
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

    override fun navigateToSubmitUrl(prefilledUrl: String) {
        if (childStack.value.active.configuration is Config.Auth) {
            navigation.replaceCurrent(Config.Main)
        }
        (childStack.value.active.instance as? RootComponent.Child.Main)
            ?.component?.navigateToSubmitUrl(prefilledUrl)
    }

    override fun navigateToSummaryDetail(summaryId: String) {
        if (childStack.value.active.configuration is Config.Auth) {
            navigation.replaceCurrent(Config.Main)
        }
        (childStack.value.active.instance as? RootComponent.Child.Main)
            ?.component?.navigateToSummaryDetail(summaryId)
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            is Config.Auth ->
                RootComponent.Child.Auth(
                    navigationRegistry.authComponentFactory.create(componentContext) {
                        navigation.replaceCurrent(Config.Main)
                    },
                )

            is Config.Main ->
                RootComponent.Child.Main(
                    DefaultMainComponent(
                        componentContext = componentContext,
                        navigationRegistry = navigationRegistry,
                    ),
                )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Auth : Config

        @Serializable
        data object Main : Config
    }
}
