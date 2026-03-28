package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.NotificationsApi
import com.po4yka.bitesizereader.domain.repository.NotificationRepository
import org.koin.core.annotation.Single

@Single(binds = [NotificationRepository::class])
class NotificationRepositoryImpl(
    private val api: NotificationsApi,
) : NotificationRepository {
    override suspend fun registerDeviceToken(
        token: String,
        platform: String,
        deviceId: String?,
    ): Result<Unit> =
        runCatching {
            api.registerDevice(token, platform, deviceId)
        }
}
