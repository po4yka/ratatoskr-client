package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.TelegramLinkStatus as GeneratedTelegramLinkStatus
import com.po4yka.ratatoskr.domain.model.TelegramLinkStatus

fun GeneratedTelegramLinkStatus.toDomain(): TelegramLinkStatus {
    return TelegramLinkStatus(
        linked = linked,
        username = username,
        telegramId = telegramUserId,
    )
}
