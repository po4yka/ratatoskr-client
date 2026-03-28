package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.bitesizereader.domain.model.ResolvedChannel

fun ResolveChannelResponseDto.toDomain(): ResolvedChannel =
    ResolvedChannel(
        username = username,
        title = title,
        description = description,
        subscriberCount = subscriberCount,
    )
