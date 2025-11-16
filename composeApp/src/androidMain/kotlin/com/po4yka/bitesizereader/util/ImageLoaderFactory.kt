package com.po4yka.bitesizereader.util

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.po4yka.bitesizereader.util.ImageCacheConfig.CACHE_DIRECTORY
import com.po4yka.bitesizereader.util.ImageCacheConfig.CROSSFADE_DURATION_MS
import com.po4yka.bitesizereader.util.ImageCacheConfig.DISK_CACHE_SIZE_MB
import com.po4yka.bitesizereader.util.ImageCacheConfig.MEMORY_CACHE_SIZE_MB

/**
 * Factory for creating configured ImageLoader instances
 */
object ImageLoaderFactory {

    /**
     * Create an optimized ImageLoader for the app
     */
    fun create(context: Context, enableDebugLogging: Boolean = false): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // Use 25% of available memory
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve(CACHE_DIRECTORY))
                    .maxSizeBytes(DISK_CACHE_SIZE_MB * 1024 * 1024L)
                    .build()
            }
            .crossfade(CROSSFADE_DURATION_MS)
            .respectCacheHeaders(false) // Use our own cache policy
            .apply {
                if (enableDebugLogging) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
