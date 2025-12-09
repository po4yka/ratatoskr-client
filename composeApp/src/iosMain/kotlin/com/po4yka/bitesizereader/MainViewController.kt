package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.theme.BiteSizeReaderTheme
import platform.UIKit.UIViewController

fun MainViewController(rootComponent: RootComponent): UIViewController =
    ComposeUIViewController {
        BiteSizeReaderTheme {
            App(
                rootComponent = rootComponent,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
