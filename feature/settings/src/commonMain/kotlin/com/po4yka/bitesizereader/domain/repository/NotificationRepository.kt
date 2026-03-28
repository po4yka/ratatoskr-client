package com.po4yka.bitesizereader.domain.repository

interface NotificationRepository {
    /** Register a device push token with the server. */
    suspend fun registerDeviceToken(
        token: String,
        platform: String,
        deviceId: String? = null,
    ): Result<Unit>
}
