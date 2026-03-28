package com.po4yka.bitesizereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val root = retainedComponent { DefaultRootComponent(it) }

        setContent {
            App(
                rootComponent = root,
            )
        }
    }
}
