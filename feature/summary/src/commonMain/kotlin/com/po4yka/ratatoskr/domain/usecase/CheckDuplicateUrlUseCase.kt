package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DuplicateCheckResult
import com.po4yka.ratatoskr.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class CheckDuplicateUrlUseCase(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(url: String): DuplicateCheckResult {
        return searchRepository.checkDuplicateUrl(url)
    }
}
