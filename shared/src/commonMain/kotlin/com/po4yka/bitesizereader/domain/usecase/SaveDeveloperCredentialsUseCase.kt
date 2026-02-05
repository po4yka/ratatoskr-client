package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.DeveloperCredentials
import com.po4yka.bitesizereader.domain.repository.CredentialsRepository
import org.koin.core.annotation.Factory

@Factory
class SaveDeveloperCredentialsUseCase(private val credentialsRepository: CredentialsRepository) {
    suspend operator fun invoke(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        credentialsRepository.saveCredentials(
            DeveloperCredentials(
                userId = userId,
                clientId = clientId,
                secret = secret,
            ),
        )
    }
}
