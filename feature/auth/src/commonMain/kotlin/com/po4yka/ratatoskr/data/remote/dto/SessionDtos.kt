package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Session information matching OpenAPI SessionInfo schema.
 * Represents an active refresh token session.
 */
@Serializable
data class SessionInfoDto(
    @SerialName("id") val id: Long,
    @SerialName("clientId") val clientId: String? = null,
    @SerialName("deviceInfo") val deviceInfo: String? = null,
    @SerialName("ipAddress") val ipAddress: String? = null,
    @SerialName("lastUsedAt") val lastUsedAt: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("isCurrent") val isCurrent: Boolean = false,
)

/**
 * Response containing list of active sessions.
 */
@Serializable
data class SessionListResponseDto(
    @SerialName("sessions") val sessions: List<SessionInfoDto>,
)

/**
 * Client secret information for server-to-server auth.
 * Matches OpenAPI ClientSecretInfo schema.
 */
@Serializable
data class ClientSecretInfoDto(
    @SerialName("id") val id: Long,
    @SerialName("user_id") val userId: Long,
    @SerialName("client_id") val clientId: String,
    @SerialName("status") val status: String, // "active", "revoked", "expired", "locked"
    @SerialName("label") val label: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("last_used_at") val lastUsedAt: String? = null,
    @SerialName("failed_attempts") val failedAttempts: Int = 0,
    @SerialName("locked_until") val lockedUntil: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

/**
 * Request to create a new client secret.
 */
@Serializable
data class SecretKeyCreateRequestDto(
    @SerialName("user_id") val userId: Long,
    @SerialName("client_id") val clientId: String,
    @SerialName("label") val label: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    /** Optional client-provided secret; if omitted server generates */
    @SerialName("secret") val secret: String? = null,
    @SerialName("username") val username: String? = null,
)

/**
 * Response after creating a secret key.
 * Contains the plaintext secret (only returned once).
 */
@Serializable
data class SecretKeyCreateResponseDto(
    /** Plaintext secret (only returned once) */
    @SerialName("secret") val secret: String,
    @SerialName("key") val key: ClientSecretInfoDto,
)

/**
 * Request to rotate an existing secret key.
 */
@Serializable
data class SecretKeyRotateRequestDto(
    @SerialName("label") val label: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("secret") val secret: String? = null,
)

/**
 * Request to revoke a secret key.
 */
@Serializable
data class SecretKeyRevokeRequestDto(
    @SerialName("reason") val reason: String? = null,
)

/**
 * Response containing a single secret key action result.
 */
@Serializable
data class SecretKeyActionResponseDto(
    @SerialName("key") val key: ClientSecretInfoDto,
)

/**
 * Response containing list of secret keys.
 */
@Serializable
data class SecretKeyListResponseDto(
    @SerialName("keys") val keys: List<ClientSecretInfoDto>,
)
