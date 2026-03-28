package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class PrefetchContentUseCase(private val repository: SummaryRepository) {
    /**
     * Prefetch full article content for recent unread summaries that don't have cached content.
     *
     * @param maxItems Maximum number of articles to prefetch
     * @return Number of articles successfully prefetched
     */
    suspend operator fun invoke(maxItems: Int = DEFAULT_PREFETCH_COUNT): Int {
        return repository.prefetchContent(maxItems)
    }

    companion object {
        const val DEFAULT_PREFETCH_COUNT = 5
    }
}
