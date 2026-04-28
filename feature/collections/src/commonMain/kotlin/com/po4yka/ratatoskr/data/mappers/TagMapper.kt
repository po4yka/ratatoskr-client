package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.TagDto
import com.po4yka.ratatoskr.domain.model.Tag

fun TagDto.toDomain(): Tag =
    Tag(
        id = id,
        name = name,
        color = color,
        summaryCount = summaryCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
