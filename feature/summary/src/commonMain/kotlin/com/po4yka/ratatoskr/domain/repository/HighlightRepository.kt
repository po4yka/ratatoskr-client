package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.Highlight
import com.po4yka.ratatoskr.domain.model.HighlightColor
import kotlinx.coroutines.flow.Flow

interface HighlightRepository {
    fun getHighlightsForSummary(summaryId: String): Flow<List<Highlight>>

    suspend fun addHighlight(
        summaryId: String,
        text: String,
        nodeOffset: Int,
        color: HighlightColor,
    ): Highlight

    suspend fun removeHighlight(id: String)

    suspend fun updateNote(
        highlightId: String,
        note: String?,
    )

    suspend fun updateColor(
        highlightId: String,
        color: HighlightColor,
    )
}
