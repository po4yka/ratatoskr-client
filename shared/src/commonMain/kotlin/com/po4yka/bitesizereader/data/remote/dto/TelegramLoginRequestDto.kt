package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelegramLoginRequestDto(
    @SerialName("id") val telegramUserId: Long,
    @SerialName("hash") val authHash: String,
    @SerialName("auth_date") val authDate: Long,
    @SerialName("username") val username: String?,
    @SerialName("first_name") val firstName: String?,
    @SerialName("last_name") val lastName: String?,
    @SerialName("photo_url") val photoUrl: String?,
    @SerialName("client_id") val clientId: String,
)
