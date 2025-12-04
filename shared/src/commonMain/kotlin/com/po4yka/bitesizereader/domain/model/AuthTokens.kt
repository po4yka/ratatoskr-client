package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val expiresAt: Instant
)
