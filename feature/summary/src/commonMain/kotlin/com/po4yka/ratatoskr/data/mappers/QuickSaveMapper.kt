package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.QuickSaveData
import com.po4yka.ratatoskr.domain.model.QuickSaveResult

fun QuickSaveData.toDomain(): QuickSaveResult =
    QuickSaveResult(
        requestId = requestId,
        status = status,
        title = title,
        url = url,
        isDuplicate = duplicate,
        summaryId = summaryId,
        attachedTags = tagsAttached.orEmpty(),
    )
