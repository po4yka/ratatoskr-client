package com.po4yka.ratatoskr.util

/**
 * Image cache configuration constants
 */
object ImageCacheConfig {
    /**
     * Maximum memory cache size in MB
     */
    const val MEMORY_CACHE_SIZE_MB = 50

    /**
     * Maximum disk cache size in MB. 64 MiB matches the value documented in
     * `docs/tasks/issues/tune-coil-image-loader-cache-and-crossfade.md` — large
     * enough to retain ~200 thumbnails at full retina resolution without
     * displacing user-app data on low-storage devices.
     */
    const val DISK_CACHE_SIZE_MB = 64

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
     * Crossfade animation duration in milliseconds. 150 ms is short enough to
     * feel snappy on list-scroll thumbnail loads without being so abrupt that
     * the user notices the swap.
     */
    const val CROSSFADE_DURATION_MS = 150
}
