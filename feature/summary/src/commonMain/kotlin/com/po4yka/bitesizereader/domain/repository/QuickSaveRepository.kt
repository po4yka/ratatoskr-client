package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.QuickSaveResult

interface QuickSaveRepository {
    suspend fun quickSave(
        url: String,
        title: String? = null,
        selectedText: String? = null,
        tagNames: List<String> = emptyList(),
        summarize: Boolean = true,
    ): QuickSaveResult
}
