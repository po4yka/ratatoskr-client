package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class LinkTelegramUseCase(private val repository: UserRepository) {
    suspend fun begin(): String {
        return repository.beginTelegramLink()
    }

    suspend fun complete(
        nonce: String,
        telegramAuth: TelegramLoginRequestDto,
    ): TelegramLinkStatus {
        return repository.completeTelegramLink(nonce, telegramAuth)
    }
}
