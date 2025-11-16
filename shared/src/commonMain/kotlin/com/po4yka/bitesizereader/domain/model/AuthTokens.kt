@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model for JWT authentication tokens.
 */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Int, // seconds
    val expiresAt: Instant,
)
