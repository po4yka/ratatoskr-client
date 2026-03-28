package com.po4yka.bitesizereader.feature.collections.navigation

import com.po4yka.bitesizereader.navigation.AppRoute

object CollectionsRoutes {
    const val FEATURE_ID = "collections"
    const val SCREEN_LIST = "list"
    const val SCREEN_VIEW = "view"

    fun list(): AppRoute = AppRoute(FEATURE_ID, SCREEN_LIST)

    fun view(collectionId: String): AppRoute = AppRoute(FEATURE_ID, SCREEN_VIEW, collectionId)
}
