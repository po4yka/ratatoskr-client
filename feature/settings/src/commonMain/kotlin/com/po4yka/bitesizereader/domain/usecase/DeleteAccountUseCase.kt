package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.feature.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteAccountUseCase(private val repository: AuthRepository) {
    /**
     * Permanently delete the user's account.
     * This action cannot be undone.
     */
    suspend operator fun invoke() {
        repository.deleteAccount()
    }
}
