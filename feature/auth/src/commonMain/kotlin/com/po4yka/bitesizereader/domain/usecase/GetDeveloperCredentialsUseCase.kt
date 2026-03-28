package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.DeveloperCredentials
import com.po4yka.bitesizereader.domain.repository.CredentialsRepository
import org.koin.core.annotation.Factory

@Factory
class GetDeveloperCredentialsUseCase(private val credentialsRepository: CredentialsRepository) {
    suspend operator fun invoke(): DeveloperCredentials? {
        return credentialsRepository.getCredentials()
    }
}
