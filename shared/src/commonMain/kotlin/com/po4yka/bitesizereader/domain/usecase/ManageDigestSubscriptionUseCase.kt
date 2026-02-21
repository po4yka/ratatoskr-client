package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.DigestRepository
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Factory

@Factory
class ManageDigestSubscriptionUseCase(private val repository: DigestRepository) {
    suspend fun subscribe(channelUsername: String): JsonObject {
        return repository.subscribe(channelUsername)
    }

    suspend fun unsubscribe(channelUsername: String): JsonObject {
        return repository.unsubscribe(channelUsername)
    }
}
