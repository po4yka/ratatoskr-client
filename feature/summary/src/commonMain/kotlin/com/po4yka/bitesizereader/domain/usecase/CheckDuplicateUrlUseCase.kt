package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

data class DuplicateCheckResult(
    val isDuplicate: Boolean,
    val existingSummaryId: String?,
)

@Factory
class CheckDuplicateUrlUseCase(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(url: String): DuplicateCheckResult {
        return searchRepository.checkDuplicateUrl(url)
    }
}
