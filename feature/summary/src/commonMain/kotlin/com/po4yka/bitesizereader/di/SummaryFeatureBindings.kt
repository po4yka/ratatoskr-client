package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.feature.summary.data.sync.HighlightPendingOperationHandler
import com.po4yka.bitesizereader.feature.summary.data.sync.HighlightSyncItemApplier
import com.po4yka.bitesizereader.feature.summary.data.sync.SummaryFeedbackPendingOperationHandler
import com.po4yka.bitesizereader.feature.summary.data.sync.SummaryPendingOperationHandler
import com.po4yka.bitesizereader.feature.summary.data.sync.SummarySyncItemApplier
import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainScreen
import com.po4yka.bitesizereader.navigation.MainTab
import com.po4yka.bitesizereader.presentation.navigation.DefaultSearchComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultSubmitURLComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultSummaryDetailComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultSummaryListComponent
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionDelegate
import com.po4yka.bitesizereader.presentation.viewmodel.RecommendationsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalController
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import com.po4yka.bitesizereader.sync.PendingOperationHandler
import com.po4yka.bitesizereader.sync.SyncItemApplier
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
                logoutUseCase = get(),
                networkMonitor = get(),
            )
        }
        single {
            val koin = getKoin()
            SummaryListRouteEntry(
                viewModelFactory = { koin.get<SummaryListViewModel>() },
                recommendationsViewModelFactory = { koin.get<RecommendationsViewModel>() },
                readingGoalControllerFactory = { koin.get<ReadingGoalController>() },
            )
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            SearchRouteEntry(viewModelFactory = { koin.get<SearchViewModel>() })
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            SummaryDetailRouteEntry(viewModelFactory = { koin.get<SummaryDetailViewModel>() })
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            SubmitUrlRouteEntry(viewModelFactory = { koin.get<SubmitURLViewModel>() })
        } bind MainRouteEntry::class
    }

private class SummaryListRouteEntry(
    private val viewModelFactory: () -> SummaryListViewModel,
    private val recommendationsViewModelFactory: () -> RecommendationsViewModel,
    private val readingGoalControllerFactory: () -> ReadingGoalController,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.SUMMARY_LIST
    override val tab: MainTab = MainTab.SUMMARY_LIST
    override val defaultRoute: MainRoute = MainRoute.SummaryList()

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.SummaryList)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultSummaryListComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        recommendationsViewModelFactory = recommendationsViewModelFactory,
                        readingGoalControllerFactory = readingGoalControllerFactory,
                        onSummarySelected = navigator::openSummaryDetail,
                        onSubmitUrl = { navigator.openSubmitUrl() },
                        onCreateDigest = navigator::openCustomDigestCreate,
                    ),
            )
        }
}

private class SearchRouteEntry(
    private val viewModelFactory: () -> SearchViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.SEARCH
    override val tab: MainTab = MainTab.SEARCH
    override val defaultRoute: MainRoute = MainRoute.Search

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.Search)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultSearchComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        onSummarySelected = navigator::openSummaryDetail,
                    ),
            )
        }
}

private class SummaryDetailRouteEntry(
    private val viewModelFactory: () -> SummaryDetailViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.SUMMARY_DETAIL
    override val tab: MainTab? = null
    override val defaultRoute: MainRoute? = null

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.SummaryDetail)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultSummaryDetailComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        summaryId = it.summaryId,
                        onBack = navigator::goBack,
                    ),
            )
        }
}

private class SubmitUrlRouteEntry(
    private val viewModelFactory: () -> SubmitURLViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.SUBMIT_URL
    override val tab: MainTab? = null
    override val defaultRoute: MainRoute? = null

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.SubmitURL)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultSubmitURLComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        prefilledUrl = it.prefilledUrl,
                        onBack = navigator::goBack,
                        onNavigateToSummary = { summaryId ->
                            navigator.goBack()
                            navigator.openSummaryDetail(summaryId)
                        },
                    ),
            )
        }
}
