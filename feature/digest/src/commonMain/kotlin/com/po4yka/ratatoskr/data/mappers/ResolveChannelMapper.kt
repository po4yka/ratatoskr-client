package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.ratatoskr.domain.model.ResolvedChannel

fun ResolveChannelResponseDto.toDomain(): ResolvedChannel =
    ResolvedChannel(
        username = username,
        title = title,
        description = description,
        subscriberCount = subscriberCount,
    )
