package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.domain.repository.AuthRepository

class LoginWithTelegramUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(authData: AuthRequestDto) {
        repository.login(authData)
    }
}