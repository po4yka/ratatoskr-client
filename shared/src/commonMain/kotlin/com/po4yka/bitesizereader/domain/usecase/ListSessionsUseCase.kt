package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class ListSessionsUseCase(private val repository: AuthRepository) {
    /**
     * Get a list of all active sessions for the current user.
     */
    suspend operator fun invoke(): List<Session> {
        return repository.listSessions()
    }
}
