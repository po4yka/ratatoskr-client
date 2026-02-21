package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.CollectionsApi
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemCreateRequest
import org.koin.core.annotation.Factory

@Factory
class AddToCollectionUseCase(private val collectionsApi: CollectionsApi) {
    suspend operator fun invoke(collectionId: String, summaryId: String) {
        collectionsApi.addItem(
            id = collectionId.toInt(),
            request = CollectionItemCreateRequest(summaryId = summaryId.toInt()),
        )
    }
}
