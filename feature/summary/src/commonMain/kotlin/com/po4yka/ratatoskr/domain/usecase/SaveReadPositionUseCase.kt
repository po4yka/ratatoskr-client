package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class SaveReadPositionUseCase(
    private val repository: SummaryRepository,
) {
    suspend operator fun invoke(
        id: String,
        position: Int,
        offset: Int,
    ) {
        repository.saveReadPosition(id, position, offset)
    }
}
