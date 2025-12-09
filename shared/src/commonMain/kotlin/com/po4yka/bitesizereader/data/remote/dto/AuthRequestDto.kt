package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    @SerialName("id") val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("auth_date") val authDate: Long,
    @SerialName("hash") val hash: String,
)
