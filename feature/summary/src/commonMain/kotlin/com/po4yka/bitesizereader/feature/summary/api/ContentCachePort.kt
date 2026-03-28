package com.po4yka.bitesizereader.feature.summary.api

interface ContentCachePort {
    suspend fun getCacheSize(): Long

    suspend fun clearContentCache()
}
