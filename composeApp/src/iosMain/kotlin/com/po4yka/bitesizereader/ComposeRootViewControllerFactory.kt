package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import platform.UIKit.UIViewController

/**
 * Swift-visible wrapper for hosting the shared Compose UI.
 *
 * Kotlin top-level functions in the app pod are not exposed reliably to Swift in this setup,
 * so iOS uses this explicit factory instead.
 */
class ComposeRootViewControllerFactory {
    private val compositionRoot = iosCompositionRoot()

    fun make(rootComponent: RootComponent): UIViewController =
        ComposeUIViewController {
            App(
                rootComponent = rootComponent,
                imageUrlTransformer = compositionRoot.imageUrlTransformer(),
                modifier = Modifier.fillMaxSize(),
            )
        }
}
