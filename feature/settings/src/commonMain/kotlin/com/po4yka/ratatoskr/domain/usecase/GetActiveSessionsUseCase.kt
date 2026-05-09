package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Session
import com.po4yka.ratatoskr.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class GetActiveSessionsUseCase(private val repository: AuthRepository) {
    /**
     * Get a list of all active sessions for the current user.
     */
    suspend operator fun invoke(): List<Session> {
        return repository.listSessions()
    }
}
