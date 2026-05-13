package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.NotificationsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.DeviceRegistrationPayload
import com.po4yka.ratatoskr.domain.repository.NotificationRepository
import org.koin.core.annotation.Single

@Single(binds = [NotificationRepository::class])
class NotificationRepositoryImpl : NotificationRepository {
    override suspend fun registerDeviceToken(
        token: String,
        platform: String,
        deviceId: String?,
    ) {
        val platformEnum = DeviceRegistrationPayload.Platform.entries.firstOrNull {
            it.name.equals(platform, ignoreCase = true)
        } ?: DeviceRegistrationPayload.Platform.ANDROID
        NotificationsApi.registerDevice(
            body = DeviceRegistrationPayload(
                token = token,
                platform = platformEnum,
                deviceId = deviceId,
            ),
        ).unwrap()
    }
}
