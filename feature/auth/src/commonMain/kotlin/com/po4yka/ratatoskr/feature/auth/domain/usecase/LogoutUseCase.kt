package com.po4yka.ratatoskr.feature.auth.domain.usecase

import com.po4yka.ratatoskr.feature.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LogoutUseCase(private val repository: AuthRepository) {
    /**
     * Logout the user.
     * @param revokeToken If true, also revoke the refresh token on the server.
     */
    suspend operator fun invoke(revokeToken: Boolean = false) {
        if (revokeToken) {
            repository.logoutWithRevoke()
        } else {
            repository.logout()
        }
    }
}
