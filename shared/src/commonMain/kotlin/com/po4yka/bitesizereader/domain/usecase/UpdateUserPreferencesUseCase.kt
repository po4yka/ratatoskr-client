package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateUserPreferencesUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(langPreference: String? = null): UserPreferences {
        return repository.updatePreferences(langPreference = langPreference)
    }
}
