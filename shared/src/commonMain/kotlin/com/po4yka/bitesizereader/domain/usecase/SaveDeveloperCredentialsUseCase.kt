package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.local.SecureStorage
import org.koin.core.annotation.Factory

@Factory
class SaveDeveloperCredentialsUseCase(private val secureStorage: SecureStorage) {
    suspend operator fun invoke(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        secureStorage.saveDeveloperCredentials(userId, clientId, secret)
    }
}
