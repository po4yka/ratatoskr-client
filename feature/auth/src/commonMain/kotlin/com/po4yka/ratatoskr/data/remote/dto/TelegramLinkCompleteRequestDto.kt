package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelegramLinkCompleteRequestDto(
    val nonce: String,
    @SerialName("telegram_auth") val telegramAuth: TelegramLoginRequestDto,
)
