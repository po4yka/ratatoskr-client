package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class UnsubscribeFromDigestChannelUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(channelUsername: String): DigestSubscriptionInfo {
        return repository.unsubscribe(channelUsername)
    }
}
