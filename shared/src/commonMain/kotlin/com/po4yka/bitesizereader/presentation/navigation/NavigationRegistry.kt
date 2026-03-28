package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestCreateViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.DigestViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.RecommendationsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.StatsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import kotlinx.serialization.Serializable
import org.koin.core.Koin
import org.koin.mp.KoinPlatform

interface ComponentFactory<out TComponent, in TInput> {
    fun create(
        componentContext: ComponentContext,
        input: TInput,
    ): TComponent
}

fun interface RouteFactory<in TRoute, out TChild> {
    fun create(
        route: TRoute,
        componentContext: ComponentContext,
    ): TChild?
}

interface FeatureEntry<in TRoute, out TChild> {
    val routeFactory: RouteFactory<TRoute, TChild>
}

interface MainTabEntry : FeatureEntry<MainRoute, MainComponent.Child> {
    val tab: MainComponent.Tab?
    val defaultRoute: MainRoute?
}

@Serializable
sealed interface MainRoute {
    @Serializable
    data class SummaryList(val collectionId: String? = null) : MainRoute

    @Serializable
    data class SummaryDetail(val summaryId: String) : MainRoute

    @Serializable
    data object Collections : MainRoute

    @Serializable
    data class CollectionView(val collectionId: String) : MainRoute

    @Serializable
    data object Stats : MainRoute

    @Serializable
    data object Settings : MainRoute

    @Serializable
    data object Search : MainRoute

    @Serializable
    data class SubmitURL(val prefilledUrl: String? = null) : MainRoute

    @Serializable
    data object Digest : MainRoute

    @Serializable
    data object CustomDigestCreate : MainRoute

    @Serializable
    data class CustomDigestView(val digestId: String) : MainRoute
}

interface MainNavigator {
    fun goBack()

    fun openSummaryDetail(summaryId: String)

    fun openSubmitUrl(prefilledUrl: String = "")

    fun openCustomDigestCreate()

    fun openCustomDigestView(digestId: String)

    fun openCollectionView(collectionId: String)

    fun openDigest()
}

internal class MainFeatureRegistry(
    private val entries: List<MainTabEntry>,
) {
    private val tabRoutes: Map<MainComponent.Tab, MainRoute> =
        entries.mapNotNull { entry ->
            val tab = entry.tab ?: return@mapNotNull null
            val defaultRoute =
                requireNotNull(entry.defaultRoute) {
                    "Main tab entry $tab must declare a default route"
                }
            tab to defaultRoute
        }.toMap()

    fun createChild(
        route: MainRoute,
        componentContext: ComponentContext,
    ): MainComponent.Child =
        entries.firstNotNullOfOrNull { entry ->
            entry.routeFactory.create(route, componentContext)
        } ?: error("No main feature entry registered for route $route")

    fun routeForTab(tab: MainComponent.Tab): MainRoute =
        tabRoutes[tab] ?: error("No main feature entry registered for tab $tab")
}

class DefaultNavigationRegistry private constructor(
    private val koin: Koin,
) {
    val authRepository: AuthRepository
        get() = koin.get()

    fun createReadingGoalViewModel(): ReadingGoalViewModel = koin.get()

    val authComponentFactory =
        object : ComponentFactory<AuthComponent, () -> Unit> {
            override fun create(
                componentContext: ComponentContext,
                input: () -> Unit,
            ): AuthComponent =
                DefaultAuthComponent(
                    componentContext = componentContext,
                    viewModelFactory = { koin.get<AuthViewModel>() },
                    onLoginSuccessCallback = input,
                )
        }

    fun createMainFeatureEntries(navigator: MainNavigator): List<MainTabEntry> =
        listOf(
            mainTabEntry(
                tab = MainComponent.Tab.SUMMARY_LIST,
                defaultRoute = MainRoute.SummaryList(),
            ) { route, componentContext ->
                (route as? MainRoute.SummaryList)?.let {
                    MainComponent.Child.SummaryList(
                        DefaultSummaryListComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<SummaryListViewModel>() },
                            recommendationsViewModelFactory = { koin.get<RecommendationsViewModel>() },
                            onSummarySelected = navigator::openSummaryDetail,
                            onSubmitUrl = { navigator.openSubmitUrl() },
                            onCreateDigest = navigator::openCustomDigestCreate,
                        ),
                    )
                }
            },
            mainTabEntry(
                tab = MainComponent.Tab.SEARCH,
                defaultRoute = MainRoute.Search,
            ) { route, componentContext ->
                (route as? MainRoute.Search)?.let {
                    MainComponent.Child.Search(
                        DefaultSearchComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<SearchViewModel>() },
                            onSummarySelected = navigator::openSummaryDetail,
                        ),
                    )
                }
            },
            mainTabEntry(
                tab = MainComponent.Tab.COLLECTIONS,
                defaultRoute = MainRoute.Collections,
            ) { route, componentContext ->
                (route as? MainRoute.Collections)?.let {
                    MainComponent.Child.Collections(
                        DefaultCollectionsComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<CollectionsViewModel>() },
                            onCollectionSelected = navigator::openCollectionView,
                        ),
                    )
                }
            },
            mainTabEntry(
                tab = MainComponent.Tab.STATS,
                defaultRoute = MainRoute.Stats,
            ) { route, componentContext ->
                (route as? MainRoute.Stats)?.let {
                    MainComponent.Child.Stats(
                        DefaultStatsComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<StatsViewModel>() },
                        ),
                    )
                }
            },
            mainTabEntry(
                tab = MainComponent.Tab.SETTINGS,
                defaultRoute = MainRoute.Settings,
            ) { route, componentContext ->
                (route as? MainRoute.Settings)?.let {
                    MainComponent.Child.Settings(
                        DefaultSettingsComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<SettingsViewModel>() },
                            onDigest = navigator::openDigest,
                        ),
                    )
                }
            },
            routeEntry { route, componentContext ->
                (route as? MainRoute.SummaryDetail)?.let {
                    MainComponent.Child.SummaryDetail(
                        DefaultSummaryDetailComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<SummaryDetailViewModel>() },
                            summaryId = it.summaryId,
                            onBack = navigator::goBack,
                        ),
                    )
                }
            },
            routeEntry { route, componentContext ->
                (route as? MainRoute.CollectionView)?.let {
                    MainComponent.Child.CollectionView(
                        DefaultCollectionViewComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<CollectionViewViewModel>() },
                            collectionId = it.collectionId,
                            onBack = navigator::goBack,
                            onNavigateToSummary = navigator::openSummaryDetail,
                            onCollectionDeleted = navigator::goBack,
                        ),
                    )
                }
            },
            routeEntry { route, componentContext ->
                (route as? MainRoute.SubmitURL)?.let {
                    MainComponent.Child.SubmitURL(
                        DefaultSubmitURLComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<SubmitURLViewModel>() },
                            prefilledUrl = it.prefilledUrl,
                            onBack = navigator::goBack,
                            onNavigateToSummary = { summaryId ->
                                navigator.goBack()
                                navigator.openSummaryDetail(summaryId)
                            },
                        ),
                    )
                }
            },
            routeEntry { route, componentContext ->
                (route as? MainRoute.Digest)?.let {
                    MainComponent.Child.Digest(
                        DefaultDigestComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<DigestViewModel>() },
                            onBack = navigator::goBack,
                        ),
                    )
                }
            },
            routeEntry { route, componentContext ->
                (route as? MainRoute.CustomDigestCreate)?.let {
                    MainComponent.Child.CustomDigestCreate(
                        DefaultCustomDigestCreateComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<CustomDigestCreateViewModel>() },
                            onBack = navigator::goBack,
                            onDigestCreated = { digestId ->
                                navigator.goBack()
                                navigator.openCustomDigestView(digestId)
                            },
                        ),
                    )
                }
            },
            routeEntry { route, componentContext ->
                (route as? MainRoute.CustomDigestView)?.let {
                    MainComponent.Child.CustomDigestView(
                        DefaultCustomDigestViewComponent(
                            componentContext = componentContext,
                            viewModelFactory = { koin.get<CustomDigestViewViewModel>() },
                            digestId = it.digestId,
                            onBack = navigator::goBack,
                        ),
                    )
                }
            },
        )

    companion object {
        fun fromKoin(): DefaultNavigationRegistry = DefaultNavigationRegistry(KoinPlatform.getKoin())
    }
}

private fun mainTabEntry(
    tab: MainComponent.Tab,
    defaultRoute: MainRoute,
    routeFactory: RouteFactory<MainRoute, MainComponent.Child>,
): MainTabEntry =
    object : MainTabEntry {
        override val tab: MainComponent.Tab = tab
        override val defaultRoute: MainRoute = defaultRoute
        override val routeFactory: RouteFactory<MainRoute, MainComponent.Child> = routeFactory
    }

private fun routeEntry(routeFactory: RouteFactory<MainRoute, MainComponent.Child>): MainTabEntry =
    object : MainTabEntry {
        override val tab: MainComponent.Tab? = null
        override val defaultRoute: MainRoute? = null
        override val routeFactory: RouteFactory<MainRoute, MainComponent.Child> = routeFactory
    }
