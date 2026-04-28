package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class GetRecentSearchesUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(limit: Int = 10): List<String> {
        return repository.getRecentSearches(limit)
    }
}
