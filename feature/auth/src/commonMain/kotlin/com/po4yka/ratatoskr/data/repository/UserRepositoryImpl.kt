package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.AuthenticationApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.TelegramLinkCompleteRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.domain.model.TelegramLinkData
import com.po4yka.ratatoskr.domain.model.TelegramLinkStatus
import com.po4yka.ratatoskr.domain.repository.UserRepository
import io.ktor.client.plugins.ClientRequestException
import org.koin.core.annotation.Single

@Single(binds = [UserRepository::class])
class UserRepositoryImpl : UserRepository {
    override suspend fun getTelegramLinkStatus(): TelegramLinkStatus {
        try {
            val status = AuthenticationApi.getTelegramLinkStatusV1AuthMeTelegramGet().unwrap().data
                ?: throw IllegalStateException("Failed to get link status")
            return status.toDomain()
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 404) {
                // 404 means no link exists or endpoint not found; treat as not linked
                return TelegramLinkStatus(linked = false)
            }
            throw e
        }
    }

    override suspend fun unlinkTelegram(): TelegramLinkStatus {
        val status = AuthenticationApi.unlinkTelegramV1AuthMeTelegramDelete().unwrap().data
            ?: throw IllegalStateException("Failed to unlink Telegram")
        return status.toDomain()
    }

    override suspend fun beginTelegramLink(): String {
        val response = AuthenticationApi.beginTelegramLinkV1AuthMeTelegramLinkPost().unwrap().data
            ?: throw IllegalStateException("Failed to begin linking")
        return response.nonce
    }

    override suspend fun completeTelegramLink(
        nonce: String,
        telegramAuth: TelegramLinkData,
    ): TelegramLinkStatus {
        val request =
            TelegramLinkCompleteRequest(
                id = telegramAuth.telegramUserId,
                hash = telegramAuth.authHash,
                authDate = telegramAuth.authDate,
                username = telegramAuth.username,
                firstName = telegramAuth.firstName,
                lastName = telegramAuth.lastName,
                photoUrl = telegramAuth.photoUrl,
                clientId = telegramAuth.clientId,
                nonce = nonce,
            )
        val status = AuthenticationApi.completeTelegramLinkV1AuthMeTelegramCompletePost(request).unwrap().data
            ?: throw IllegalStateException("Failed to complete linking")
        return status.toDomain()
    }
}
