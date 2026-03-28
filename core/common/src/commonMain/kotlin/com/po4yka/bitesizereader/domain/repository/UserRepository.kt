package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.TelegramLinkData
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus

interface UserRepository {
    suspend fun getTelegramLinkStatus(): TelegramLinkStatus

    suspend fun unlinkTelegram(): TelegramLinkStatus

    suspend fun beginTelegramLink(): String

    suspend fun completeTelegramLink(
        nonce: String,
        telegramAuth: TelegramLinkData,
    ): TelegramLinkStatus
}
