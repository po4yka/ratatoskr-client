package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class GetDigestChannelsUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): DigestSubscriptionInfo {
        return repository.getChannels()
    }
}
