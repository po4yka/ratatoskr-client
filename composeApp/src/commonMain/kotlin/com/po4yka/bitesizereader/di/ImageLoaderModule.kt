package com.po4yka.bitesizereader.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import io.ktor.client.HttpClient
import okio.FileSystem
import org.koin.dsl.module

val imageLoaderModule =
    module {
        single {
            getImageLoader(get(), get())
        }
    }

@OptIn(ExperimentalCoilApi::class)
fun getImageLoader(
    context: PlatformContext,
    httpClient: HttpClient,
): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient))
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                .maxSizeBytes(1024L * 1024L * 100L) // 100MB
                .build()
        }
        .crossfade(true)
        .build()
}
