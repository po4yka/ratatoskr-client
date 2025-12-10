package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class MarkSummaryAsReadUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(id: String) {
        repository.markAsRead(id)
    }
}
