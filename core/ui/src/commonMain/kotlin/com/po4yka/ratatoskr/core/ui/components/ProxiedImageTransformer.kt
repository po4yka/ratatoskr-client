package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.mikepenz.markdown.model.ImageData
import com.mikepenz.markdown.model.ImageTransformer

/**
 * An [ImageTransformer] that rewrites image URLs through the app's image proxy
 * before loading them with Coil, matching the behavior of [ProxiedImage].
 */
class ProxiedImageTransformer(
    private val imageUrlTransformer: (String) -> String,
) : ImageTransformer {
    @Composable
    override fun transform(link: String): ImageData {
        val proxiedUrl = imageUrlTransformer(link)
        val model =
            ImageRequest.Builder(LocalPlatformContext.current)
                .data(proxiedUrl)
                .size(coil3.size.Size.ORIGINAL)
                .build()
        val painter = rememberAsyncImagePainter(model = model)
        return ImageData(painter)
    }

    @Composable
    override fun intrinsicSize(painter: Painter): Size {
        var size by remember(painter) { mutableStateOf(painter.intrinsicSize) }
        if (painter is AsyncImagePainter) {
            val painterState = painter.state.collectAsState()
            val intrinsicSize = painterState.value.painter?.intrinsicSize
            intrinsicSize?.also { size = it }
        }
        return size
    }
}
