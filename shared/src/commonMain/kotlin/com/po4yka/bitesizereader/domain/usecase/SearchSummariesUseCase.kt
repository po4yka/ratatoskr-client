package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SearchRepository

class SearchSummariesUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(query: String, page: Int, pageSize: Int): List<Summary> {
        return repository.search(query, page, pageSize)
    }
}