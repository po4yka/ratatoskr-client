package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.NotificationRepository
import org.koin.core.annotation.Factory

@Factory
class RegisterDeviceTokenUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(
        token: String,
        platform: String,
        deviceId: String? = null,
    ): Result<Unit> {
        return repository.registerDeviceToken(token, platform, deviceId)
    }
}
