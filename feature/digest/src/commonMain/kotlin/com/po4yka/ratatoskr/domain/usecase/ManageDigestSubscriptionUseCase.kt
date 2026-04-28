package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class ManageDigestSubscriptionUseCase(private val repository: DigestRepository) {
    suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        return repository.subscribe(channelUsername)
    }

    suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        return repository.unsubscribe(channelUsername)
    }
}
