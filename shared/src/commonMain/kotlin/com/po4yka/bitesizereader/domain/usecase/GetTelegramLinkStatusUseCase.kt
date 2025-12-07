package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.repository.UserRepository

class GetTelegramLinkStatusUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): TelegramLinkStatus {
        return repository.getTelegramLinkStatus()
    }
}
