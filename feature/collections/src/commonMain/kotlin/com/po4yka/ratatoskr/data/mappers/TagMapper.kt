package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.Tag as GeneratedTag
import com.po4yka.ratatoskr.domain.model.Tag

fun GeneratedTag.toDomain(): Tag =
    Tag(
        id = id.toInt(),
        name = name,
        color = color,
        summaryCount = summaryCount.toInt(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )
