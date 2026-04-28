package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TelegramLinkBeginResponseDto(
    val nonce: String,
)
