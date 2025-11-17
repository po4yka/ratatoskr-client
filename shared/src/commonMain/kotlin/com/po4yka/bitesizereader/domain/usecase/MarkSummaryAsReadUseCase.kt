package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository

/**
 * Use case for marking a summary as read/unread
 */
class MarkSummaryAsReadUseCase(
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(
        id: Int,
        isRead: Boolean,
    ): Result<Unit> {
        return summaryRepository.markAsRead(id, isRead)
    }
}
