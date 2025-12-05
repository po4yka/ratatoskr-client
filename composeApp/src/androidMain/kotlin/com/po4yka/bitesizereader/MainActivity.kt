package com.po4yka.bitesizereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.po4yka.bitesizereader.presentation.navigation.DefaultRootComponent
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.screens.AuthScreen
import com.po4yka.bitesizereader.ui.screens.SummaryListScreen
import com.po4yka.bitesizereader.ui.theme.BiteSizeReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(defaultComponentContext())

        setContent {
            BiteSizeReaderTheme {
                val childStack by root.childStack.subscribeAsState()

                Children(
                    stack = childStack,
                ) {
                    when (val child = it.instance) {
                        is RootComponent.Child.Auth -> AuthScreen(
                            component = child.component,
                            onLoginClick = { _ ->
                                com.po4yka.bitesizereader.auth.TelegramAuthHelper.launchTelegramAuth(this@MainActivity)
                            }
                        )
                        is RootComponent.Child.Main -> SummaryListScreen(child.component.summaryListComponent)
                    }
                }
            }
        }
    }
}
