package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.feature.summary.data.sync.HighlightPendingOperationHandler
import com.po4yka.ratatoskr.feature.summary.data.sync.HighlightSyncItemApplier
import com.po4yka.ratatoskr.feature.summary.data.sync.SummaryFeedbackPendingOperationHandler
import com.po4yka.ratatoskr.feature.summary.data.sync.SummaryPendingOperationHandler
import com.po4yka.ratatoskr.feature.summary.data.sync.SummarySyncItemApplier
import com.po4yka.ratatoskr.feature.summary.navigation.SummaryRoutes
import com.po4yka.ratatoskr.feature.summary.ui.screens.SearchScreen
import com.po4yka.ratatoskr.feature.summary.ui.screens.SubmitURLScreen
import com.po4yka.ratatoskr.feature.summary.ui.screens.SummaryDetailScreen
import com.po4yka.ratatoskr.feature.summary.ui.screens.SummaryListScreen
import com.po4yka.ratatoskr.feature.sync.api.PendingOperationHandler
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainChildDescriptor
import com.po4yka.ratatoskr.navigation.MainNavigator
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import com.po4yka.ratatoskr.navigation.MainTab
import com.po4yka.ratatoskr.presentation.navigation.DefaultSearchComponent
import com.po4yka.ratatoskr.presentation.navigation.DefaultSubmitURLComponent
import com.po4yka.ratatoskr.presentation.navigation.DefaultSummaryDetailComponent
import com.po4yka.ratatoskr.presentation.navigation.DefaultSummaryListComponent
import com.po4yka.ratatoskr.presentation.viewmodel.CollectionDelegate
import com.po4yka.ratatoskr.presentation.viewmodel.RecommendationsViewModel
import com.po4yka.ratatoskr.presentation.viewmodel.ReadingGoalController
import com.po4yka.ratatoskr.presentation.viewmodel.SearchViewModel
import com.po4yka.ratatoskr.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.ratatoskr.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.ratatoskr.presentation.viewmodel.SummaryListViewModel
import org.koin.core.Koin
import org.koin.dsl.bind
import org.koin.dsl.module

val summaryFeatureBindingsModule =
    module {
        factory {
            RecommendationsViewModel(
                getRecommendationsUseCase = get(),
                refreshRecommendationsUseCase = get(),
                dismissRecommendationUseCase = get(),
            )
        }
        factory {
            SearchViewModel(
                searchSummariesUseCase = get(),
                semanticSearchUseCase = get(),
                searchHistoryManager = get(),
                getSearchInsightsUseCase = get(),
            )
        }
        factory {
            SubmitURLViewModel(
                processingService = get(),
                getRequestsUseCase = get(),
                retryRequestUseCase = get(),
                checkDuplicateUrlUseCase = get(),
            )
        }
        factory {
            SummaryDetailViewModel(
                readingSessionDelegate = get(),
                audioDelegate = get(),
                highlightDelegate = get(),
                feedbackDelegate = get(),
                collectionDelegate = get(),
                getSummaryByIdUseCase = get(),
                getSummaryContentUseCase = get(),
                refreshFullContentUseCase = get(),
                deleteSummaryUseCase = get(),
                toggleFavoriteUseCase = get(),
                readingPreferencesRepository = get(),
                networkMonitor = get(),
                exportSummaryUseCase = get(),
                shareManager = get(),
            )
        }
        factory {
            CollectionDelegate(
                collectionRepository = get(),
                addToCollectionUseCase = get(),
            )
        }
        single { SummarySyncItemApplier(database = get()) } bind SyncItemApplier::class
        single { HighlightSyncItemApplier(database = get()) } bind SyncItemApplier::class
        single { SummaryPendingOperationHandler() } bind PendingOperationHandler::class
        single {
            SummaryFeedbackPendingOperationHandler(
                database = get(),
                summariesApi = get(),
            )
        } bind PendingOperationHandler::class
        single {
            HighlightPendingOperationHandler(
                database = get(),
                highlightsApi = get(),
            )
        } bind PendingOperationHandler::class
        factory {
            SummaryListViewModel(
                getFilteredSummariesUseCase = get(),
                searchSummariesUseCase = get(),
                markSummaryAsReadUseCase = get(),
                deleteSummaryUseCase = get(),
                archiveSummaryUseCase = get(),
                getAvailableTagsUseCase = get(),
                searchHistoryManager = get(),
                syncDataUseCase = get(),
                toggleFavoriteUseCase = get(),
                authSessionPort = get(),
                networkMonitor = get(),
            )
        }
    }

fun summaryRouteEntries(
    koin: Koin,
    digestCreateRoute: () -> AppRoute,
): List<MainRouteEntry> =
    listOf(
        SummaryListRouteEntry(
            viewModelFactory = { koin.get<SummaryListViewModel>() },
            recommendationsViewModelFactory = { koin.get<RecommendationsViewModel>() },
            readingGoalControllerFactory = { koin.get<ReadingGoalController>() },
            digestCreateRoute = digestCreateRoute,
        ),
        SearchRouteEntry(viewModelFactory = { koin.get<SearchViewModel>() }),
        SummaryDetailRouteEntry(viewModelFactory = { koin.get<SummaryDetailViewModel>() }),
        SubmitUrlRouteEntry(viewModelFactory = { koin.get<SubmitURLViewModel>() }),
    )

private class SummaryListRouteEntry(
    private val viewModelFactory: () -> SummaryListViewModel,
    private val recommendationsViewModelFactory: () -> RecommendationsViewModel,
    private val readingGoalControllerFactory: () -> ReadingGoalController,
    private val digestCreateRoute: () -> AppRoute,
) : MainRouteEntry {
    override val tab: MainTab = MainTab.SUMMARY_LIST
    override val defaultRoute: AppRoute = SummaryRoutes.list()

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == SummaryRoutes.FEATURE_ID && it.screenId == SummaryRoutes.SCREEN_LIST
        }?.let {
            val summaryListComponent =
                DefaultSummaryListComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    recommendationsViewModelFactory = recommendationsViewModelFactory,
                    readingGoalControllerFactory = readingGoalControllerFactory,
                    onSummarySelected = { summaryId -> navigator.open(SummaryRoutes.detail(summaryId)) },
                    onSubmitUrl = { navigator.open(SummaryRoutes.submitUrl()) },
                    onCreateDigest = { navigator.open(digestCreateRoute()) },
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = summaryListComponent,
                render = { SummaryListScreen(component = summaryListComponent) },
            )
        }
}

private class SearchRouteEntry(
    private val viewModelFactory: () -> SearchViewModel,
) : MainRouteEntry {
    override val tab: MainTab = MainTab.SEARCH
    override val defaultRoute: AppRoute = SummaryRoutes.search()

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == SummaryRoutes.FEATURE_ID && it.screenId == SummaryRoutes.SCREEN_SEARCH
        }?.let {
            val searchComponent =
                DefaultSearchComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    onSummarySelected = { summaryId -> navigator.open(SummaryRoutes.detail(summaryId)) },
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = searchComponent,
                render = { SearchScreen(component = searchComponent) },
            )
        }
}

private class SummaryDetailRouteEntry(
    private val viewModelFactory: () -> SummaryDetailViewModel,
) : MainRouteEntry {
    override val tab: MainTab? = null
    override val defaultRoute: AppRoute? = null

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == SummaryRoutes.FEATURE_ID && it.screenId == SummaryRoutes.SCREEN_DETAIL
        }?.let {
            val summaryDetailComponent =
                DefaultSummaryDetailComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    summaryId = requireNotNull(route.argument),
                    onBack = navigator::goBack,
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = summaryDetailComponent,
                render = { SummaryDetailScreen(component = summaryDetailComponent) },
            )
        }
}

private class SubmitUrlRouteEntry(
    private val viewModelFactory: () -> SubmitURLViewModel,
) : MainRouteEntry {
    override val tab: MainTab? = null
    override val defaultRoute: AppRoute? = null

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == SummaryRoutes.FEATURE_ID && it.screenId == SummaryRoutes.SCREEN_SUBMIT_URL
        }?.let {
            val submitUrlComponent =
                DefaultSubmitURLComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    prefilledUrl = route.argument,
                    onBack = navigator::goBack,
                    onNavigateToSummary = { summaryId ->
                        navigator.goBack()
                        navigator.open(SummaryRoutes.detail(summaryId))
                    },
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = submitUrlComponent,
                render = { SubmitURLScreen(component = submitUrlComponent) },
            )
        }
}
