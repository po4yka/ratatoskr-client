package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelegramLinkStatusDto(
    val linked: Boolean,
    val username: String? = null,
    @SerialName("telegram_id") val telegramId: Long? = null,
)
