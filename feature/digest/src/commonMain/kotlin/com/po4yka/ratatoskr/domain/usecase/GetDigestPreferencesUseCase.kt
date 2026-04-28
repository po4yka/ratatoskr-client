package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DigestPreferences
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class GetDigestPreferencesUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): DigestPreferences {
        return repository.getPreferences()
    }
}
