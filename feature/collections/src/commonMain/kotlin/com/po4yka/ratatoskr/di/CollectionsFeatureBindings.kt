package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.feature.collections.data.sync.SummaryTagSyncItemApplier
import com.po4yka.ratatoskr.feature.collections.data.sync.TagSyncItemApplier
import com.po4yka.ratatoskr.feature.collections.navigation.CollectionsRoutes
import com.po4yka.ratatoskr.feature.collections.ui.screens.CollectionsScreen
import com.po4yka.ratatoskr.feature.collections.ui.screens.CollectionViewScreen
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainChildDescriptor
import com.po4yka.ratatoskr.navigation.MainNavigator
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import com.po4yka.ratatoskr.navigation.MainTab
import com.po4yka.ratatoskr.presentation.navigation.DefaultCollectionViewComponent
import com.po4yka.ratatoskr.presentation.navigation.DefaultCollectionsComponent
import com.po4yka.ratatoskr.presentation.viewmodel.CollectionViewViewModel
import com.po4yka.ratatoskr.presentation.viewmodel.CollectionsViewModel
import org.koin.core.Koin
import org.koin.dsl.bind
import org.koin.dsl.module

val collectionsFeatureBindingsModule =
    module {
        factory {
            CollectionsViewModel(
                collectionRepository = get(),
                createCollectionUseCase = get(),
            )
        }
        factory {
            CollectionViewViewModel(
                getCollectionUseCase = get(),
                getCollectionItemsUseCase = get(),
                updateCollectionUseCase = get(),
                deleteCollectionUseCase = get(),
                getCollectionAclUseCase = get(),
                manageCollaboratorUseCase = get(),
                createInviteLinkUseCase = get(),
            )
        }
        single { TagSyncItemApplier(database = get()) } bind SyncItemApplier::class
        single { SummaryTagSyncItemApplier(database = get()) } bind SyncItemApplier::class
    }

fun collectionsRouteEntries(
    koin: Koin,
    summaryDetailRoute: (String) -> AppRoute,
): List<MainRouteEntry> =
    listOf(
        CollectionsRouteEntry(viewModelFactory = { koin.get<CollectionsViewModel>() }),
        CollectionViewRouteEntry(
            viewModelFactory = { koin.get<CollectionViewViewModel>() },
            summaryDetailRoute = summaryDetailRoute,
        ),
    )

private class CollectionsRouteEntry(
    private val viewModelFactory: () -> CollectionsViewModel,
) : MainRouteEntry {
    override val tab: MainTab = MainTab.COLLECTIONS
    override val defaultRoute: AppRoute = CollectionsRoutes.list()

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == CollectionsRoutes.FEATURE_ID && it.screenId == CollectionsRoutes.SCREEN_LIST
        }?.let {
            val collectionsComponent =
                DefaultCollectionsComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    onCollectionSelected = { collectionId -> navigator.open(CollectionsRoutes.view(collectionId)) },
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = collectionsComponent,
                render = { CollectionsScreen(component = collectionsComponent) },
            )
        }
}

private class CollectionViewRouteEntry(
    private val viewModelFactory: () -> CollectionViewViewModel,
    private val summaryDetailRoute: (String) -> AppRoute,
) : MainRouteEntry {
    override val tab: MainTab? = null
    override val defaultRoute: AppRoute? = null

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == CollectionsRoutes.FEATURE_ID && it.screenId == CollectionsRoutes.SCREEN_VIEW
        }?.let {
            val collectionViewComponent =
                DefaultCollectionViewComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    collectionId = requireNotNull(route.argument),
                    onBack = navigator::goBack,
                    onNavigateToSummary = { summaryId -> navigator.open(summaryDetailRoute(summaryId)) },
                    onCollectionDeleted = navigator::goBack,
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = collectionViewComponent,
                render = { CollectionViewScreen(component = collectionViewComponent) },
            )
        }
}
