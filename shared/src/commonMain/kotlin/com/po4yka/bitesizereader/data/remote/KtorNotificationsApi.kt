package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DeviceRegistrationRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [NotificationsApi::class])
class KtorNotificationsApi(private val client: HttpClient) : NotificationsApi {
    override suspend fun registerDevice(
        token: String,
        platform: String,
        deviceId: String?,
    ): ApiResponseDto<SuccessResponse> {
        return client.post("v1/notifications/device") {
            contentType(ContentType.Application.Json)
            setBody(DeviceRegistrationRequestDto(token, platform, deviceId))
        }.body()
    }
}
