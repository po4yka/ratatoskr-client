package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository

class MarkSummaryAsReadUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(id: String) {
        repository.markAsRead(id)
    }
}
