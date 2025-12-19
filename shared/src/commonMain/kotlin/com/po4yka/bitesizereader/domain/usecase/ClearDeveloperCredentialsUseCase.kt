package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.local.SecureStorage
import org.koin.core.annotation.Factory

@Factory
class ClearDeveloperCredentialsUseCase(private val secureStorage: SecureStorage) {
    suspend operator fun invoke() {
        secureStorage.clearDeveloperCredentials()
    }
}
