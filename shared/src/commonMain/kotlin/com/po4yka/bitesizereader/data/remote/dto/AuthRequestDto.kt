package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Telegram authentication request (existing format).
 */
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

/**
 * Apple Sign In request matching OpenAPI AppleLoginRequest schema.
 */
@Serializable
data class AppleLoginRequestDto(
    /** Apple ID token from Sign in with Apple */
    @SerialName("id_token") val idToken: String,
    /** Client application identifier */
    @SerialName("client_id") val clientId: String,
    /** Authorization code from Apple (optional) */
    @SerialName("authorization_code") val authorizationCode: String? = null,
    /** User's given name (provided on first sign-in only) */
    @SerialName("given_name") val givenName: String? = null,
    /** User's family name (provided on first sign-in only) */
    @SerialName("family_name") val familyName: String? = null,
)

/**
 * Google Sign In request matching OpenAPI GoogleLoginRequest schema.
 */
@Serializable
data class GoogleLoginRequestDto(
    /** Google ID token from Google Sign-In */
    @SerialName("id_token") val idToken: String,
    /** Client application identifier */
    @SerialName("client_id") val clientId: String,
)

/**
 * Logout request - requires refresh token to revoke.
 */
@Serializable
data class LogoutRequestDto(
    @SerialName("refresh_token") val refreshToken: String,
)
