package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import com.po4yka.ratatoskr.feature.sync.domain.repository.SyncContentPrefetcher
import org.koin.core.annotation.Single

@Single(binds = [SyncContentPrefetcher::class])
class PrefetchContentUseCase(private val repository: SummaryRepository) : SyncContentPrefetcher {
    override suspend fun prefetchRecentContent(maxItems: Int): Int {
        return repository.prefetchContent(maxItems)
    }
}
