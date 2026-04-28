package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.local.SecureStorage
import com.po4yka.ratatoskr.domain.model.DeveloperCredentials
import com.po4yka.ratatoskr.domain.repository.CredentialsRepository
import org.koin.core.annotation.Single

@Single(binds = [CredentialsRepository::class])
class CredentialsRepositoryImpl(
    private val secureStorage: SecureStorage,
) : CredentialsRepository {
    override suspend fun saveCredentials(credentials: DeveloperCredentials) {
        secureStorage.saveDeveloperCredentials(
            userId = credentials.userId,
            clientId = credentials.clientId,
            secret = credentials.secret,
        )
    }

    override suspend fun getCredentials(): DeveloperCredentials? {
        return secureStorage.getDeveloperCredentials()?.let {
            DeveloperCredentials(
                userId = it.userId,
                clientId = it.clientId,
                secret = it.secret,
            )
        }
    }

    override suspend fun clearCredentials() {
        secureStorage.clearDeveloperCredentials()
    }
}
