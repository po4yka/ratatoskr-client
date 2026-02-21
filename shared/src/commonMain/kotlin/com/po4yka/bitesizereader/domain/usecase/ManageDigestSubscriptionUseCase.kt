package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.mappers.toDigestSubscriptionInfo
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class ManageDigestSubscriptionUseCase(private val repository: DigestRepository) {
    suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        return repository.subscribe(channelUsername).toDigestSubscriptionInfo()
    }

    suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        return repository.unsubscribe(channelUsername).toDigestSubscriptionInfo()
    }
}
