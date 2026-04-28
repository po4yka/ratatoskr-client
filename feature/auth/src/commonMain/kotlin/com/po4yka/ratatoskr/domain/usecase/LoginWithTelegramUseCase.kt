package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.TelegramAuthData
import com.po4yka.ratatoskr.feature.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LoginWithTelegramUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(authData: TelegramAuthData) {
        repository.login(authData)
    }
}
