package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.mappers.toDigestHistoryItems
import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class GetDigestHistoryUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 20): List<DigestHistoryItem> {
        return repository.getHistory(page, pageSize).toDigestHistoryItems()
    }
}
