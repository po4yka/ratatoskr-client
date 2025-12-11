package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    /**
     * Login with Google Sign In.
     * @return User preferences if available from login response.
     */
    suspend operator fun invoke(
        idToken: String,
        clientId: String,
    ): UserPreferences? {
        return repository.loginWithGoogle(
            idToken = idToken,
            clientId = clientId,
        )
    }
}
