package com.po4yka.bitesizereader.domain.repository

interface SyncContentPrefetcher {
    suspend fun prefetchRecentContent(maxItems: Int = DEFAULT_PREFETCH_COUNT): Int

    companion object {
        const val DEFAULT_PREFETCH_COUNT = 5
    }
}
