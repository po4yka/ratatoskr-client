package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
interface MainComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class SummaryList(val component: SummaryListComponent) : Child()
        data class Collections(val component: CollectionsComponent) : Child()
        data class Settings(val component: SettingsComponent) : Child()
    }
}

class DefaultMainComponent(
    componentContext: ComponentContext
) : MainComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, MainComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.SummaryList,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): MainComponent.Child =
        when (config) {
            is Config.SummaryList -> MainComponent.Child.SummaryList(
                DefaultSummaryListComponent { id ->
                    // TODO: Navigate to detail
                }
            )
            is Config.Collections -> MainComponent.Child.Collections(
                DefaultCollectionsComponent { id ->
                    // TODO: Filter list by collection
                }
            )
            is Config.Settings -> MainComponent.Child.Settings(
                DefaultSettingsComponent(componentContext)
            )
        }

    fun onTabSelected(config: Config) {
        navigation.bringToFront(config)
    }

    @kotlinx.serialization.Serializable
    sealed interface Config {
        @kotlinx.serialization.Serializable
        data object SummaryList : Config

        @kotlinx.serialization.Serializable
        data object Collections : Config

        @kotlinx.serialization.Serializable
        data object Settings : Config
    }
}
