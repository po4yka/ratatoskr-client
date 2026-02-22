package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class GetDigestChannelsUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): DigestSubscriptionInfo {
        return repository.getChannels()
    }
}
