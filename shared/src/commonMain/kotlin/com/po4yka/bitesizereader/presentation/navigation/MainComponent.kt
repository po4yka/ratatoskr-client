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
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

interface MainComponent {
    val childStack: Value<ChildStack<*, Child>>
    val readingGoalViewModel: ReadingGoalViewModel

    fun navigateToTab(tab: Tab)

    fun navigateToSubmitUrl(prefilledUrl: String)

    fun navigateToSummaryDetail(summaryId: String)

    fun navigateToCustomDigestCreate()

    fun navigateToCustomDigestView(digestId: String)

    enum class Tab {
        SUMMARY_LIST,
        SEARCH,
        COLLECTIONS,
        STATS,
        SETTINGS,
    }

    sealed class Child {
        data class SummaryList(val component: SummaryListComponent) : Child()

        data class SummaryDetail(val component: SummaryDetailComponent) : Child()

        data class Search(val component: SearchComponent) : Child()

        data class Collections(val component: CollectionsComponent) : Child()

        data class CollectionView(val component: CollectionViewComponent) : Child()

        data class Stats(val component: StatsComponent) : Child()

        data class Settings(val component: SettingsComponent) : Child()

        data class SubmitURL(val component: SubmitURLComponent) : Child()

        data class Digest(val component: DigestComponent) : Child()

        data class CustomDigestCreate(val component: CustomDigestCreateComponent) : Child()

        data class CustomDigestView(val component: CustomDigestViewComponent) : Child()
    }
}

internal class DefaultMainComponent(
    componentContext: ComponentContext,
    private val navigationRegistry: DefaultNavigationRegistry = DefaultNavigationRegistry.fromKoin(),
) : MainComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<MainRoute>()
    override val readingGoalViewModel: ReadingGoalViewModel =
        retainedInstance { navigationRegistry.createReadingGoalViewModel() }

    private val navigator =
        object : MainNavigator {
            override fun goBack() {
                navigation.pop()
            }

            override fun openSummaryDetail(summaryId: String) {
                navigation.push(MainRoute.SummaryDetail(summaryId))
            }

            override fun openSubmitUrl(prefilledUrl: String) {
                navigation.push(MainRoute.SubmitURL(prefilledUrl.ifBlank { null }))
            }

            override fun openCustomDigestCreate() {
                navigation.push(MainRoute.CustomDigestCreate)
            }

            override fun openCustomDigestView(digestId: String) {
                navigation.push(MainRoute.CustomDigestView(digestId))
            }

            override fun openCollectionView(collectionId: String) {
                logger.info { "Navigate to collection view: $collectionId" }
                navigation.push(MainRoute.CollectionView(collectionId))
            }

            override fun openDigest() {
                navigation.push(MainRoute.Digest)
            }
        }

    private val featureRegistry = MainFeatureRegistry(navigationRegistry.createMainFeatureEntries(navigator))

    override val childStack: Value<ChildStack<*, MainComponent.Child>> =
        childStack(
            source = navigation,
            serializer = MainRoute.serializer(),
            initialConfiguration = MainRoute.SummaryList(),
            handleBackButton = true,
            childFactory = featureRegistry::createChild,
        )

    override fun navigateToSubmitUrl(prefilledUrl: String) {
        navigator.openSubmitUrl(prefilledUrl)
    }

    override fun navigateToSummaryDetail(summaryId: String) {
        navigator.openSummaryDetail(summaryId)
    }

    override fun navigateToCustomDigestCreate() {
        navigator.openCustomDigestCreate()
    }

    override fun navigateToCustomDigestView(digestId: String) {
        navigator.openCustomDigestView(digestId)
    }

    override fun navigateToTab(tab: MainComponent.Tab) {
        navigation.bringToFront(featureRegistry.routeForTab(tab))
    }
}
