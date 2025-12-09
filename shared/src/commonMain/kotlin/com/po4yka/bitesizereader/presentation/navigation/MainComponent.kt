package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

interface MainComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class SummaryList(val component: SummaryListComponent) : Child()
        data class SummaryDetail(val component: SummaryDetailComponent) : Child()
        data class Collections(val component: CollectionsComponent) : Child()
        data class Settings(val component: SettingsComponent) : Child()
    }
}

class DefaultMainComponent(
    componentContext: ComponentContext,
) : MainComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, MainComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.SummaryList(),
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: ComponentContext): MainComponent.Child =
        when (config) {
            is Config.SummaryList -> MainComponent.Child.SummaryList(
                DefaultSummaryListComponent { id ->
                    navigateToSummaryDetail(id)
                },
            )
            is Config.SummaryDetail -> MainComponent.Child.SummaryDetail(
                DefaultSummaryDetailComponent(
                    summaryId = config.summaryId,
                    onBack = { navigation.pop() },
                ),
            )
            is Config.Collections -> MainComponent.Child.Collections(
                DefaultCollectionsComponent { collectionId ->
                    filterByCollection(collectionId)
                },
            )
            is Config.Settings -> MainComponent.Child.Settings(
                DefaultSettingsComponent(componentContext),
            )
        }

    private fun navigateToSummaryDetail(summaryId: String) {
        navigation.push(Config.SummaryDetail(summaryId))
    }

    private fun filterByCollection(collectionId: String) {
        logger.info { "Filter by collection: $collectionId" }
        // Navigate to summary list with the collection filter applied
        navigation.bringToFront(Config.SummaryList(collectionId = collectionId))
    }

    fun onTabSelected(config: Config) {
        navigation.bringToFront(config)
    }

    @kotlinx.serialization.Serializable
    sealed interface Config {
        @kotlinx.serialization.Serializable
        data class SummaryList(val collectionId: String? = null) : Config

        @kotlinx.serialization.Serializable
        data class SummaryDetail(val summaryId: String) : Config

        @kotlinx.serialization.Serializable
        data object Collections : Config

        @kotlinx.serialization.Serializable
        data object Settings : Config
    }
}
