package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SearchRepository

class GetTrendingTopicsUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(): List<String> {
        return repository.getTrendingTopics()
    }
}
