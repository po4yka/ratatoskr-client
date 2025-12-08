package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRegistrationPayload(
    @SerialName("token") val token: String,
    @SerialName("platform") val platform: String, // "ios" or "android"
    @SerialName("device_id") val deviceId: String? = null
)

@Serializable
data class BaseResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null
)
