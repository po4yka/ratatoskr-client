package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDto
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.data.mappers.toDomain as toSummaryDomain

fun SearchResponseDto.toDomain(): List<Summary> {
    return results.map { it.toSummaryDomain() }
}