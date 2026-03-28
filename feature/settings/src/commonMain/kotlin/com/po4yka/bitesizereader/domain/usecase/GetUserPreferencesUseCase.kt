package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Factory

@Factory
class GetUserPreferencesUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserPreferences {
        return repository.getPreferences()
    }
}
