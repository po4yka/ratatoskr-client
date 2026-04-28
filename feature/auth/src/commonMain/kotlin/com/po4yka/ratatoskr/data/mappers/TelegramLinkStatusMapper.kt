package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.TelegramLinkStatusDto
import com.po4yka.ratatoskr.domain.model.TelegramLinkStatus

fun TelegramLinkStatusDto.toDomain(): TelegramLinkStatus {
    return TelegramLinkStatus(
        linked = linked,
        username = username,
        telegramId = telegramId,
    )
}
