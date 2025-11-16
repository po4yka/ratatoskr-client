package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.presentation.navigation.Screen
import com.po4yka.bitesizereader.presentation.viewmodel.*
import com.po4yka.bitesizereader.ui.screens.*
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * Main app composable with Decompose navigation
 */
@Composable
fun App(
    rootComponent: RootComponent,
    modifier: Modifier = Modifier
) {
    Children(
        stack = rootComponent.stack,
        modifier = modifier.fillMaxSize(),
        animation = stackAnimation(fade())
    ) { child ->
        when (val screen = child.instance) {
            is Screen.Auth -> {
                val viewModel: LoginViewModel = koinInject()
                AuthScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { rootComponent.navigateToSummaryList() }
                )
            }
            is Screen.SummaryList -> {
                val viewModel: SummaryListViewModel = koinInject()
                SummaryListScreen(
                    viewModel = viewModel,
                    onSummaryClick = { id -> rootComponent.navigateToSummaryDetail(id) },
                    onSubmitUrlClick = { rootComponent.navigateToSubmitUrl() },
                    onSearchClick = { rootComponent.navigateToSearch() }
                )
            }
            is Screen.SummaryDetail -> {
                val viewModel: SummaryDetailViewModel = koinInject { parametersOf(screen.id) }
                SummaryDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { rootComponent.pop() },
                    onShareClick = {
                        // TODO: Implement share functionality
                    }
                )
            }
            is Screen.SubmitUrl -> {
                val viewModel: SubmitURLViewModel = koinInject()
                SubmitURLScreen(
                    viewModel = viewModel,
                    onBackClick = { rootComponent.pop() },
                    onSuccess = { summaryId ->
                        rootComponent.pop()
                        rootComponent.navigateToSummaryDetail(summaryId)
                    }
                )
            }
            is Screen.Search -> {
                val viewModel: SearchViewModel = koinInject()
                SearchScreen(
                    viewModel = viewModel,
                    onSummaryClick = { id -> rootComponent.navigateToSummaryDetail(id) },
                    onBackClick = { rootComponent.pop() }
                )
            }
        }
    }
}