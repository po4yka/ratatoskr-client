package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Factory

@Factory
class GetUserPreferencesUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserPreferences {
        return repository.getPreferences()
    }
}
