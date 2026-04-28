package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.User
import com.po4yka.ratatoskr.feature.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}
