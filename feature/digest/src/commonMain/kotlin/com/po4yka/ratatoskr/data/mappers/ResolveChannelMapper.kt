package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.ResolveChannelData
import com.po4yka.ratatoskr.domain.model.ResolvedChannel

fun ResolveChannelData.toDomain(): ResolvedChannel =
    ResolvedChannel(
        username = username,
        title = title,
        description = description,
        subscriberCount = memberCount?.toInt(),
    )
