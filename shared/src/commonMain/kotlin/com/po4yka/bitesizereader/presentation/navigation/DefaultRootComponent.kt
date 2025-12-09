package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext, KoinComponent {
    private val navigation = StackNavigation<Config>()
    private val authRepository: AuthRepository by inject()
    private val scope: CoroutineScope by inject()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Auth,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    init {
        observeAuthState()
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

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            is Config.Auth ->
                RootComponent.Child.Auth(
                    DefaultAuthComponent(componentContext) {
                        navigation.replaceCurrent(Config.Main)
                    },
                )
            is Config.Main -> RootComponent.Child.Main(DefaultMainComponent(componentContext))
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Auth : Config

        @Serializable
        data object Main : Config
    }
}
