package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class SaveSearchQueryUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(query: String) {
        repository.saveSearchQuery(query)
    }
}
