package com.po4yka.ratatoskr.app

import com.po4yka.ratatoskr.feature.summary.navigation.SummaryRoutes
import com.po4yka.ratatoskr.presentation.navigation.RootComponent

sealed interface AppLaunchAction {
    data class SubmitUrl(val prefilledUrl: String? = null) : AppLaunchAction

    data object Search : AppLaunchAction
}

fun RootComponent.handleLaunchAction(action: AppLaunchAction) {
    val route =
        when (action) {
            is AppLaunchAction.SubmitUrl -> SummaryRoutes.submitUrl(action.prefilledUrl)
            AppLaunchAction.Search -> SummaryRoutes.search()
        }
    open(route)
}
