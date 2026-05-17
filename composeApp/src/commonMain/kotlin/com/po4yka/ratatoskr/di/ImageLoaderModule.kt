package com.po4yka.ratatoskr.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.po4yka.ratatoskr.util.ImageCacheConfig
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
                .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / ImageCacheConfig.CACHE_DIRECTORY)
                .maxSizeBytes(ImageCacheConfig.DISK_CACHE_SIZE_MB * 1024L * 1024L)
                .build()
        }
        .crossfade(ImageCacheConfig.CROSSFADE_DURATION_MS)
        .build()
}
