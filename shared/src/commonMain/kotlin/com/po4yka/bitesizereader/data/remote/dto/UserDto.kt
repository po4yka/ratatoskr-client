package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User information matching OpenAPI User schema.
 */
@Serializable
data class UserDto(
    /** User ID - may come as "id" or "user_id" depending on endpoint */
    @SerialName("user_id") val userId: Long,
    @SerialName("username") val username: String? = null,
    /** Display name for UI presentation */
    @SerialName("display_name") val displayName: String? = null,
    /** Profile photo URL */
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("client_id") val clientId: String? = null,
    @SerialName("is_owner") val isOwner: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
)
