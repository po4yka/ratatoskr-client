package com.po4yka.bitesizereader.domain.model

data class TelegramAuthData(
    val id: String,
    val firstName: String,
    val lastName: String? = null,
    val username: String? = null,
    val photoUrl: String? = null,
    val authDate: Long,
    val hash: String,
)

data class TelegramLinkData(
    val telegramUserId: Long,
    val authHash: String,
    val authDate: Long,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val photoUrl: String? = null,
    val clientId: String,
)
