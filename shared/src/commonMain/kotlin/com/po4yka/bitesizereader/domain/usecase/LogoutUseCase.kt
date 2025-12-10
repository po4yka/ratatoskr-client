package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
