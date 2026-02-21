package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.CollectionsApi
import com.po4yka.bitesizereader.data.remote.dto.CollectionCreateRequest
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollectionType
import org.koin.core.annotation.Factory

@Factory
class CreateCollectionUseCase(private val collectionsApi: CollectionsApi) {
    suspend operator fun invoke(name: String, description: String? = null): Collection {
        val response = collectionsApi.createCollection(
            CollectionCreateRequest(name = name, description = description),
        )
        val dto = response.data ?: error("Failed to create collection")
        return Collection(
            id = dto.id.toString(),
            name = dto.name,
            count = 0,
            type = CollectionType.User,
            description = dto.description,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
    }
}
