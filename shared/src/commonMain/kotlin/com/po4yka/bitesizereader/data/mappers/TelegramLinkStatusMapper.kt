package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkStatusDto
import com.po4yka.bitesizereader.domain.model.TelegramLinkStatus

fun TelegramLinkStatusDto.toDomain(): TelegramLinkStatus {
    return TelegramLinkStatus(
        linked = linked,
        username = username,
        telegramId = telegramId
    )
}
