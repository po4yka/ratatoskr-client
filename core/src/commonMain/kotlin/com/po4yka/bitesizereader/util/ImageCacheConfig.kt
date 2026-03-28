package com.po4yka.bitesizereader.util

/**
 * Image cache configuration constants
 */
object ImageCacheConfig {
    /**
     * Maximum memory cache size in MB
     */
    const val MEMORY_CACHE_SIZE_MB = 50

    /**
     * Maximum disk cache size in MB
     */
    const val DISK_CACHE_SIZE_MB = 250

    /**
     * Cache directory name
     */
    const val CACHE_DIRECTORY = "image_cache"

    /**
     * Maximum age for cached images in days
     */
    const val MAX_CACHE_AGE_DAYS = 30

    /**
     * Default placeholder color (hex)
     */
    const val PLACEHOLDER_COLOR = 0xFFE0E0E0

    /**
     * Crossfade animation duration in milliseconds
     */
    const val CROSSFADE_DURATION_MS = 300
}
