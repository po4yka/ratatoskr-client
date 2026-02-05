package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.TelegramAuthData
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LoginWithTelegramUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(authData: TelegramAuthData) {
        repository.login(authData)
    }
}
