package com.po4yka.bitesizereader.navigation

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

enum class MainTab {
    SUMMARY_LIST,
    SEARCH,
    COLLECTIONS,
    STATS,
    SETTINGS,
}

enum class MainScreen {
    SUMMARY_LIST,
    SUMMARY_DETAIL,
    SEARCH,
    COLLECTIONS,
    COLLECTION_VIEW,
    STATS,
    SETTINGS,
    SUBMIT_URL,
    DIGEST,
    CUSTOM_DIGEST_CREATE,
    CUSTOM_DIGEST_VIEW,
}

data class MainChildDescriptor(
    val screen: MainScreen,
    val component: Any,
)

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

interface MainRouteEntry {
    val screen: MainScreen
    val tab: MainTab?
    val defaultRoute: MainRoute?

    fun create(
        route: MainRoute,
        componentContext: ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor?
}
