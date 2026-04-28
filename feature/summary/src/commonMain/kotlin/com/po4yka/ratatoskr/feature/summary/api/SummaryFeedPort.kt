package com.po4yka.ratatoskr.feature.summary.api

import com.po4yka.ratatoskr.domain.model.Summary
import kotlinx.coroutines.flow.Flow

interface SummaryFeedPort {
    fun getSummaries(
        page: Int,
        pageSize: Int,
        tags: List<String>? = null,
    ): Flow<List<Summary>>
}
