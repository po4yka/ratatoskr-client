package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.UserStats
import com.po4yka.bitesizereader.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Factory

@Factory
class GetUserStatsUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserStats {
        return repository.getStats()
    }
}
