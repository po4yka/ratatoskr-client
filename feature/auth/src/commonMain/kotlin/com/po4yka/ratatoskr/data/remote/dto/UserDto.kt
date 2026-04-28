package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User information matching OpenAPI User schema.
 */
@Serializable
data class UserDto(
    @SerialName("userId") val id: Long,
    @SerialName("isOwner") val isOwner: Boolean,
    @SerialName("username") val username: String? = null,
    @SerialName("displayName") val displayName: String? = null,
    @SerialName("photoUrl") val photoUrl: String? = null,
    @SerialName("clientId") val clientId: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
)
