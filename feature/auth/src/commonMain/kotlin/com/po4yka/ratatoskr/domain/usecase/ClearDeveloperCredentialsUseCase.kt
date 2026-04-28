package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.CredentialsRepository
import org.koin.core.annotation.Factory

@Factory
class ClearDeveloperCredentialsUseCase(private val credentialsRepository: CredentialsRepository) {
    suspend operator fun invoke() {
        credentialsRepository.clearCredentials()
    }
}
