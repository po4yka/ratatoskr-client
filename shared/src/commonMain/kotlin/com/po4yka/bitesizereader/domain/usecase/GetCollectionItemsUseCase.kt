package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.CollectionRepository

class GetCollectionItemsUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        collectionId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): List<Summary> {
        return repository.getCollectionItems(collectionId, limit, offset)
    }
}
