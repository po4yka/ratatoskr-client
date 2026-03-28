package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.feature.collections.data.sync.SummaryTagSyncItemApplier
import com.po4yka.bitesizereader.feature.collections.data.sync.TagSyncItemApplier
import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainScreen
import com.po4yka.bitesizereader.navigation.MainTab
import com.po4yka.bitesizereader.presentation.navigation.DefaultCollectionViewComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultCollectionsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionsViewModel
import com.po4yka.bitesizereader.sync.SyncItemApplier
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
        single {
            val koin = getKoin()
            CollectionsRouteEntry(viewModelFactory = { koin.get<CollectionsViewModel>() })
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            CollectionViewRouteEntry(viewModelFactory = { koin.get<CollectionViewViewModel>() })
        } bind MainRouteEntry::class
    }

private class CollectionsRouteEntry(
    private val viewModelFactory: () -> CollectionsViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.COLLECTIONS
    override val tab: MainTab = MainTab.COLLECTIONS
    override val defaultRoute: MainRoute = MainRoute.Collections

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.Collections)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultCollectionsComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        onCollectionSelected = navigator::openCollectionView,
                    ),
            )
        }
}

private class CollectionViewRouteEntry(
    private val viewModelFactory: () -> CollectionViewViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.COLLECTION_VIEW
    override val tab: MainTab? = null
    override val defaultRoute: MainRoute? = null

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.CollectionView)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultCollectionViewComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        collectionId = it.collectionId,
                        onBack = navigator::goBack,
                        onNavigateToSummary = navigator::openSummaryDetail,
                        onCollectionDeleted = navigator::goBack,
                    ),
            )
        }
}
