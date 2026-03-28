package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.gabrieldrn.carbon.Carbon

val LocalImageUrlTransformer = compositionLocalOf<(String) -> String> { { it } }

@Suppress("FunctionNaming")
@Composable
fun ProxiedImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val imageUrlTransformer = LocalImageUrlTransformer.current
    val proxiedUrl = remember(imageUrl, imageUrlTransformer) { imageUrlTransformer(imageUrl) }

    AsyncImage(
        model = proxiedUrl,
        contentDescription = contentDescription,
        modifier = modifier.background(Carbon.theme.layer02),
        contentScale = contentScale,
    )
}
