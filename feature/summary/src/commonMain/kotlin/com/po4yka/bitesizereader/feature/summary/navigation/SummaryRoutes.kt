package com.po4yka.bitesizereader.feature.summary.navigation

import com.po4yka.bitesizereader.navigation.AppRoute

object SummaryRoutes {
    const val FEATURE_ID = "summary"
    const val SCREEN_LIST = "list"
    const val SCREEN_SEARCH = "search"
    const val SCREEN_DETAIL = "detail"
    const val SCREEN_SUBMIT_URL = "submit-url"

    fun list(collectionId: String? = null): AppRoute = AppRoute(FEATURE_ID, SCREEN_LIST, collectionId)

    fun search(): AppRoute = AppRoute(FEATURE_ID, SCREEN_SEARCH)

    fun detail(summaryId: String): AppRoute = AppRoute(FEATURE_ID, SCREEN_DETAIL, summaryId)

    fun submitUrl(prefilledUrl: String? = null): AppRoute = AppRoute(FEATURE_ID, SCREEN_SUBMIT_URL, prefilledUrl)
}
