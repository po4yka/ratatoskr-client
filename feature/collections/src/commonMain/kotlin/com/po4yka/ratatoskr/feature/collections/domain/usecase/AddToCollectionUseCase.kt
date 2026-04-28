package com.po4yka.ratatoskr.feature.collections.domain.usecase

import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class AddToCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        collectionId: String,
        summaryId: String,
    ) {
        repository.addToCollection(collectionId = collectionId, summaryId = summaryId)
    }
}
