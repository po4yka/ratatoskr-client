package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.domain.repository.SyncContentPrefetcher
import org.koin.core.annotation.Single

@Single(binds = [SyncContentPrefetcher::class])
class PrefetchContentUseCase(private val repository: SummaryRepository) : SyncContentPrefetcher {
    /**
     * Prefetch full article content for recent unread summaries that don't have cached content.
     *
     * @param maxItems Maximum number of articles to prefetch
     * @return Number of articles successfully prefetched
     */
    override suspend fun prefetchRecentContent(maxItems: Int): Int {
        return repository.prefetchContent(maxItems)
    }
}
