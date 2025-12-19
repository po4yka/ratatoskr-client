package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.local.DeveloperCredentials
import com.po4yka.bitesizereader.data.local.SecureStorage
import org.koin.core.annotation.Factory

@Factory
class GetDeveloperCredentialsUseCase(private val secureStorage: SecureStorage) {
    suspend operator fun invoke(): DeveloperCredentials? {
        return secureStorage.getDeveloperCredentials()
    }
}
