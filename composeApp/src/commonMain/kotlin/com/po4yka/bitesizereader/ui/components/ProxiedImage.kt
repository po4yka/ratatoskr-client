package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.loading.SmallLoading
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
    val proxiedUrl = getProxiedImageUrlUseCase(imageUrl)

    AsyncImage(
        model = proxiedUrl,
        contentDescription = contentDescription,
        modifier = modifier.background(Carbon.theme.layer02),
        contentScale = contentScale,
    )
}
