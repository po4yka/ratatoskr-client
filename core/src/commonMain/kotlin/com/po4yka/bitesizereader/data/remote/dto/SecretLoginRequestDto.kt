package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretLoginRequestDto(
    @SerialName("user_id") val userId: Int,
    @SerialName("client_id") val clientId: String,
    val secret: String,
    val username: String? = null,
)
