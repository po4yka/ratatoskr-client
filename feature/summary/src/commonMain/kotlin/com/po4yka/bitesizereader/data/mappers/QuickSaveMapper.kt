package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.QuickSaveResponseDto
import com.po4yka.bitesizereader.domain.model.QuickSaveResult

fun QuickSaveResponseDto.toDomain(): QuickSaveResult =
    QuickSaveResult(
        requestId = requestId,
        status = status,
        title = title,
        url = url,
        isDuplicate = duplicate,
        summaryId = summaryId,
        attachedTags = tagsAttached.orEmpty(),
    )
