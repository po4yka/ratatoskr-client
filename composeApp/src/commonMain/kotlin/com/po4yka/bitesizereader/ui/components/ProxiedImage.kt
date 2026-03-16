package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.usecase.GetProxiedImageUrlUseCase
import org.koin.compose.koinInject

@Suppress("FunctionNaming")
@Composable
fun ProxiedImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val getProxiedImageUrlUseCase = koinInject<GetProxiedImageUrlUseCase>()
    val proxiedUrl = remember(imageUrl) { getProxiedImageUrlUseCase(imageUrl) }

    AsyncImage(
        model = proxiedUrl,
        contentDescription = contentDescription,
        modifier = modifier.background(Carbon.theme.layer02),
        contentScale = contentScale,
    )
}
