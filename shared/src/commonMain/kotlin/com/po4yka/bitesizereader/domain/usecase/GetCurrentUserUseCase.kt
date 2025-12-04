package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}
