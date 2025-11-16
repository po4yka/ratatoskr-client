package com.po4yka.bitesizereader.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Screen configurations for navigation
 */
@Serializable
sealed interface Screen {
    @Serializable
    data object Auth : Screen

    @Serializable
    data object SummaryList : Screen

    @Serializable
    data class SummaryDetail(val id: Int) : Screen

    @Serializable
    data object SubmitUrl : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Settings : Screen
}
