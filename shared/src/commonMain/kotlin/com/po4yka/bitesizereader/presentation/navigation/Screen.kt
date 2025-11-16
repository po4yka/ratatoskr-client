package com.po4yka.bitesizereader.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Screen configurations for navigation
 */
sealed interface Screen {
    @Serializable
    data object Login : Screen

    @Serializable
    data object SummaryList : Screen

    @Serializable
    data class SummaryDetail(val id: Int) : Screen

    @Serializable
    data object SubmitURL : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Settings : Screen
}
