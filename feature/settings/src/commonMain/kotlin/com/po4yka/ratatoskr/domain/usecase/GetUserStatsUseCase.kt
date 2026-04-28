package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.UserStats
import com.po4yka.ratatoskr.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Factory

@Factory
class GetUserStatsUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserStats {
        return repository.getStats()
    }
}
