package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.repository.CollectionRepository

class GetCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: String): Collection? {
        return repository.getCollection(id)
    }
}
