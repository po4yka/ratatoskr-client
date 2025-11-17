package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.repository.AuthRepository

/**
 * Use case for logging in with Telegram
 */
class LoginWithTelegramUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        telegramUserId: Long,
        authHash: String,
        authDate: Long,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String,
    ): Result<Pair<AuthTokens, User>> {
        return authRepository.loginWithTelegram(
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
}
