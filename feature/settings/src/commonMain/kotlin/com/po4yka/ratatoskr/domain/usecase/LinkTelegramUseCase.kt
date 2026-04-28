package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.TelegramLinkData
import com.po4yka.ratatoskr.domain.model.TelegramLinkStatus
import com.po4yka.ratatoskr.domain.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class LinkTelegramUseCase(private val repository: UserRepository) {
    suspend fun begin(): String {
        return repository.beginTelegramLink()
    }

    suspend fun complete(
        nonce: String,
        telegramAuth: TelegramLinkData,
    ): TelegramLinkStatus {
        return repository.completeTelegramLink(nonce, telegramAuth)
    }
}
