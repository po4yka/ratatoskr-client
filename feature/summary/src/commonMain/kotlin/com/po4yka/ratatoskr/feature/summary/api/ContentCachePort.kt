package com.po4yka.ratatoskr.feature.summary.api

interface ContentCachePort {
    suspend fun getCacheSize(): Long

    suspend fun clearContentCache()
}
