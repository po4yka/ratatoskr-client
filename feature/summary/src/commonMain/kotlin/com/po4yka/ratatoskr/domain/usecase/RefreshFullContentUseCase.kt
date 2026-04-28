package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class RefreshFullContentUseCase(private val repository: SummaryRepository) {
    /**
     * Fetches fresh content if the cache is stale. Returns the new content, or null if already fresh.
     */
    suspend operator fun invoke(id: String): String? = repository.refreshFullContentIfStale(id)
}
