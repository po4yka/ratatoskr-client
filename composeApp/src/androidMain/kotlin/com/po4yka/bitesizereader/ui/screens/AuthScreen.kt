package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.po4yka.bitesizereader.presentation.navigation.AuthComponent

@Composable
fun AuthScreen(component: AuthComponent) {
    val state by component.viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome to Bite-Size Reader")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { 
                // Trigger Telegram Login (simulated for now)
                // component.viewModel.login(...)
            }) {
                Text("Login with Telegram")
            }
        }
    }
}
