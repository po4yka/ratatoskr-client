package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.mappers.toDigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class GetDigestPreferencesUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): DigestPreferences {
        return repository.getPreferences().toDigestPreferences()
    }
}
