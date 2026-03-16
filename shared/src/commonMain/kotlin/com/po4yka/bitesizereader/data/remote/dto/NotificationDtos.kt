package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Device registration for push notifications matching backend POST /v1/notifications/device.
 */
@Serializable
data class DeviceRegistrationRequestDto(
    @SerialName("token") val token: String,
    @SerialName("platform") val platform: String,
    @SerialName("device_id") val deviceId: String? = null,
)
