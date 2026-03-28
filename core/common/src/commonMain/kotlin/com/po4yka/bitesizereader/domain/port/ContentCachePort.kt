package com.po4yka.bitesizereader.domain.port

interface ContentCachePort {
    suspend fun getCacheSize(): Long

    suspend fun clearContentCache()
}
