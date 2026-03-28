package com.po4yka.bitesizereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import com.po4yka.bitesizereader.app.assembleAppCompositionRoot
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val compositionRoot = assembleAppCompositionRoot(getKoin())
        val root = retainedComponent { compositionRoot.createRoot(it) }

        setContent {
            App(
                rootComponent = root,
                imageUrlTransformer = compositionRoot.imageUrlTransformer(),
            )
        }
    }
}
