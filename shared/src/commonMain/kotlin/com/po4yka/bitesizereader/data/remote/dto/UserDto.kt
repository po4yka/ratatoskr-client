package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("user_id") val userId: Long,
    @SerialName("username") val username: String? = null,
    @SerialName("client_id") val clientId: String? = null,
    @SerialName("is_owner") val isOwner: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
