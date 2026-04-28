package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateUserPreferencesUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(langPreference: String? = null): UserPreferences {
        return repository.updatePreferences(langPreference = langPreference)
    }
}
