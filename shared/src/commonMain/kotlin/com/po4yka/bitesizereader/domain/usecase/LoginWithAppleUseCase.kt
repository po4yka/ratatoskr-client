package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LoginWithAppleUseCase(private val repository: AuthRepository) {
    /**
     * Login with Apple Sign In.
     * @return User preferences if available from login response.
     */
    suspend operator fun invoke(
        idToken: String,
        clientId: String,
        authorizationCode: String? = null,
        givenName: String? = null,
        familyName: String? = null,
    ): UserPreferences? {
        return repository.loginWithApple(
            idToken = idToken,
            clientId = clientId,
            authorizationCode = authorizationCode,
            givenName = givenName,
            familyName = familyName,
        )
    }
}
