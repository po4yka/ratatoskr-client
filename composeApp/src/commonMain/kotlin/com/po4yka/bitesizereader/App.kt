package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.po4yka.bitesizereader.domain.usecase.GetProxiedImageUrlUseCase
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.components.LocalImageUrlTransformer
import com.po4yka.bitesizereader.ui.screens.AuthScreen
import com.po4yka.bitesizereader.ui.screens.MainScreen
import com.po4yka.bitesizereader.ui.theme.BiteSizeReaderTheme
import org.koin.compose.koinInject

/** Main app composable with Decompose navigation */
@Composable
fun App(
    rootComponent: RootComponent,
    modifier: Modifier = Modifier,
) {
    val childStack = rootComponent.childStack.subscribeAsState()
    val getProxiedImageUrlUseCase = koinInject<GetProxiedImageUrlUseCase>()

    BiteSizeReaderTheme {
        CompositionLocalProvider(
            LocalImageUrlTransformer provides getProxiedImageUrlUseCase::invoke,
        ) {
            Children(
                stack = childStack.value,
                modifier =
                    modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.Auth ->
                        AuthScreen(
                            component = instance.component,
                        )

                    is RootComponent.Child.Main -> MainScreen(component = instance.component)
                }
            }
        }
    }
}
