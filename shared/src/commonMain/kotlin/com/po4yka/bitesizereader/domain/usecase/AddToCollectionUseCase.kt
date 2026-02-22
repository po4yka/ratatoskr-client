package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class AddToCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(collectionId: String, summaryId: String) {
        repository.addToCollection(collectionId = collectionId, summaryId = summaryId)
    }
}
