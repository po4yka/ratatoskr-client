package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.repository.CollectionRepository

class UpdateCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        id: String,
        name: String? = null,
        description: String? = null,
        isPublic: Boolean? = null,
    ): Collection {
        return repository.updateCollection(id, name, description, isPublic)
    }
}
