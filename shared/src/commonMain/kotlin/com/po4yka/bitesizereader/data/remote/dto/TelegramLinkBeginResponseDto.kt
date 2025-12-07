package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TelegramLinkBeginResponseDto(
    val nonce: String
)
