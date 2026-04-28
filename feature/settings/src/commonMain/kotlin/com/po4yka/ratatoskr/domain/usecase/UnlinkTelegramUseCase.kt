package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.TelegramLinkStatus
import com.po4yka.ratatoskr.domain.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class UnlinkTelegramUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): TelegramLinkStatus {
        return repository.unlinkTelegram()
    }
}
