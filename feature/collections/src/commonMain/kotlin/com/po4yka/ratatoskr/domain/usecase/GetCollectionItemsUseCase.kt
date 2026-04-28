package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class GetCollectionItemsUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        collectionId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): List<Summary> {
        return repository.getCollectionItems(collectionId, limit, offset)
    }
}
