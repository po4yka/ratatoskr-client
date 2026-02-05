package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.UserApi
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkCompleteRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.domain.model.TelegramLinkData
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus
import com.po4yka.bitesizereader.domain.repository.UserRepository
import org.koin.core.annotation.Single

import io.ktor.client.plugins.ClientRequestException

@Single(binds = [UserRepository::class])
class UserRepositoryImpl(
    private val userApi: UserApi,
) : UserRepository {
    override suspend fun getTelegramLinkStatus(): TelegramLinkStatus {
        try {
            val response = userApi.getTelegramLinkStatus()
            if (response.success && response.data != null) {
                return response.data.toDomain()
            } else {
                throw response.error?.let { Exception(it.message) }
                    ?: Exception("Failed to get link status")
            }
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 404) {
                // 404 means no link exists or endpoint not found; treat as not linked
                return TelegramLinkStatus(linked = false)
            }
            throw e
        }
    }

    override suspend fun unlinkTelegram(): TelegramLinkStatus {
        val response = userApi.unlinkTelegram()
        if (response.success && response.data != null) {
            return response.data.toDomain()
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Failed to unlink Telegram")
        }
    }

    override suspend fun beginTelegramLink(): String {
        val response = userApi.beginTelegramLink()
        if (response.success && response.data != null) {
            return response.data.nonce
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Failed to begin linking")
        }
    }

    override suspend fun completeTelegramLink(
        nonce: String,
        telegramAuth: TelegramLinkData,
    ): TelegramLinkStatus {
        val request = TelegramLinkCompleteRequestDto(nonce, telegramAuth.toDto())
        val response = userApi.completeTelegramLink(request)
        if (response.success && response.data != null) {
            return response.data.toDomain()
        } else {
            throw response.error?.let { Exception(it.message) } ?: Exception("Failed to complete linking")
        }
    }

    private fun TelegramLinkData.toDto() =
        TelegramLoginRequestDto(
            telegramUserId = telegramUserId,
            authHash = authHash,
            authDate = authDate,
            username = username,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl,
            clientId = clientId,
        )
}
