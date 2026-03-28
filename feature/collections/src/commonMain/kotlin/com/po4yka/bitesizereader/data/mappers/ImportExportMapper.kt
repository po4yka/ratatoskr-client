package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.ImportJobDto
import com.po4yka.bitesizereader.domain.model.ImportJob

fun ImportJobDto.toDomain(): ImportJob =
    ImportJob(
        id = id,
        sourceFormat = sourceFormat,
        fileName = fileName,
        status = status,
        totalItems = totalItems,
        processedItems = processedItems,
        createdItems = createdItems,
        skippedItems = skippedItems,
        failedItems = failedItems,
        errors = errors,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
