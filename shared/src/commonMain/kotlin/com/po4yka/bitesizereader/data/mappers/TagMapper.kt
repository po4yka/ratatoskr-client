package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.TagDto
import com.po4yka.bitesizereader.domain.model.Tag

fun TagDto.toDomain(): Tag =
    Tag(
        id = id,
        name = name,
        color = color,
        summaryCount = summaryCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
