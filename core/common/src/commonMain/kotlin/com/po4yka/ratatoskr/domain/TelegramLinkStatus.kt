package com.po4yka.ratatoskr.domain.model

data class TelegramLinkStatus(
    val linked: Boolean,
    val username: String? = null,
    val telegramId: Long? = null,
)
