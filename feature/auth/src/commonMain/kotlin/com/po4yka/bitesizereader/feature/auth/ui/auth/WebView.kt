package com.po4yka.bitesizereader.feature.auth.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun WebView(
    url: String,
    modifier: Modifier = Modifier,
    onDeepLink: (String) -> Unit,
)
