package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User information matching OpenAPI User schema.
 */
@Serializable
data class UserDto(
    @SerialName("id") val id: Long,
    @SerialName("is_owner") val isOwner: Boolean,
    @SerialName("username") val username: String? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("client_id") val clientId: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
