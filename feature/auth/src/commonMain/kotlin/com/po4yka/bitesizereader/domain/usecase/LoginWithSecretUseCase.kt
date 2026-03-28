package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.feature.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LoginWithSecretUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        repository.loginWithSecret(userId, clientId, secret)
    }
}
