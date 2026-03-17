@file:OptIn(com.arkivanov.decompose.DelicateDecomposeApi::class)

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

    fun navigateToTab(tab: Tab)

    fun navigateToSubmitUrl(prefilledUrl: String)

    fun navigateToSummaryDetail(summaryId: String)

    fun navigateToCustomDigestCreate()

    fun navigateToCustomDigestView(digestId: String)

    enum class Tab {
        SUMMARY_LIST,
        SEARCH,
        COLLECTIONS,
        SETTINGS,
    }

    sealed class Child {
        data class SummaryList(val component: SummaryListComponent) : Child()

        data class SummaryDetail(val component: SummaryDetailComponent) : Child()

        data class Search(val component: SearchComponent) : Child()

        data class Collections(val component: CollectionsComponent) : Child()

        data class CollectionView(val component: CollectionViewComponent) : Child()

        data class Settings(val component: SettingsComponent) : Child()

        data class SubmitURL(val component: SubmitURLComponent) : Child()

        data class Digest(val component: DigestComponent) : Child()

        data class CustomDigestCreate(val component: CustomDigestCreateComponent) : Child()

        data class CustomDigestView(val component: CustomDigestViewComponent) : Child()
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

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): MainComponent.Child =
        when (config) {
            is Config.SummaryList ->
                MainComponent.Child.SummaryList(
                    DefaultSummaryListComponent(
                        componentContext = componentContext,
                        onSummarySelected = { id -> navigateToSummaryDetail(id) },
                        onSubmitUrl = { navigateToSubmitUrl("") },
                        onCreateDigest = { navigateToCustomDigestCreate() },
                    ),
                )
            is Config.SummaryDetail ->
                MainComponent.Child.SummaryDetail(
                    DefaultSummaryDetailComponent(
                        componentContext = componentContext,
                        summaryId = config.summaryId,
                        onBack = { navigation.pop() },
                    ),
                )
            is Config.Collections ->
                MainComponent.Child.Collections(
                    DefaultCollectionsComponent(
                        componentContext = componentContext,
                        onCollectionSelected = { collectionId -> navigateToCollectionView(collectionId) },
                    ),
                )
            is Config.CollectionView ->
                MainComponent.Child.CollectionView(
                    DefaultCollectionViewComponent(
                        componentContext = componentContext,
                        collectionId = config.collectionId,
                        onBack = { navigation.pop() },
                        onNavigateToSummary = { summaryId -> navigation.push(Config.SummaryDetail(summaryId)) },
                        onCollectionDeleted = { navigation.pop() },
                    ),
                )
            is Config.Settings ->
                MainComponent.Child.Settings(
                    DefaultSettingsComponent(
                        componentContext = componentContext,
                        onDigest = { navigateToDigest() },
                    ),
                )
            is Config.Search ->
                MainComponent.Child.Search(
                    DefaultSearchComponent(
                        componentContext = componentContext,
                        onSummarySelected = { id -> navigateToSummaryDetail(id) },
                    ),
                )
            is Config.SubmitURL ->
                MainComponent.Child.SubmitURL(
                    DefaultSubmitURLComponent(
                        componentContext = componentContext,
                        prefilledUrl = config.prefilledUrl,
                        onBack = { navigation.pop() },
                        onNavigateToSummary = { summaryId ->
                            navigation.pop()
                            navigateToSummaryDetail(summaryId)
                        },
                    ),
                )
            is Config.Digest ->
                MainComponent.Child.Digest(
                    DefaultDigestComponent(
                        componentContext = componentContext,
                        onBack = { navigation.pop() },
                    ),
                )
            is Config.CustomDigestCreate ->
                MainComponent.Child.CustomDigestCreate(
                    DefaultCustomDigestCreateComponent(
                        componentContext = componentContext,
                        onBack = { navigation.pop() },
                        onDigestCreated = { digestId ->
                            navigation.pop()
                            navigation.push(Config.CustomDigestView(digestId))
                        },
                    ),
                )
            is Config.CustomDigestView ->
                MainComponent.Child.CustomDigestView(
                    DefaultCustomDigestViewComponent(
                        componentContext = componentContext,
                        digestId = config.digestId,
                        onBack = { navigation.pop() },
                    ),
                )
        }

    override fun navigateToSubmitUrl(prefilledUrl: String) {
        navigation.push(Config.SubmitURL(prefilledUrl.ifBlank { null }))
    }

    override fun navigateToSummaryDetail(summaryId: String) {
        navigation.push(Config.SummaryDetail(summaryId))
    }

    private fun navigateToDigest() {
        navigation.push(Config.Digest)
    }

    override fun navigateToCustomDigestCreate() {
        navigation.push(Config.CustomDigestCreate)
    }

    override fun navigateToCustomDigestView(digestId: String) {
        navigation.push(Config.CustomDigestView(digestId))
    }

    private fun navigateToCollectionView(collectionId: String) {
        logger.info { "Navigate to collection view: $collectionId" }
        navigation.push(Config.CollectionView(collectionId = collectionId))
    }

    override fun navigateToTab(tab: MainComponent.Tab) {
        val config =
            when (tab) {
                MainComponent.Tab.SUMMARY_LIST -> Config.SummaryList()
                MainComponent.Tab.SEARCH -> Config.Search
                MainComponent.Tab.COLLECTIONS -> Config.Collections
                MainComponent.Tab.SETTINGS -> Config.Settings
            }
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
        data class CollectionView(val collectionId: String) : Config

        @kotlinx.serialization.Serializable
        data object Settings : Config

        @kotlinx.serialization.Serializable
        data object Search : Config

        @kotlinx.serialization.Serializable
        data class SubmitURL(val prefilledUrl: String? = null) : Config

        @kotlinx.serialization.Serializable
        data object Digest : Config

        @kotlinx.serialization.Serializable
        data object CustomDigestCreate : Config

        @kotlinx.serialization.Serializable
        data class CustomDigestView(val digestId: String) : Config
    }
}
