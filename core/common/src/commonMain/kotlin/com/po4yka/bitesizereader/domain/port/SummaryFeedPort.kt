package com.po4yka.bitesizereader.domain.port

import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.coroutines.flow.Flow

interface SummaryFeedPort {
    fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>? = null,
    ): Flow<List<Summary>>
}
