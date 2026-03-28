package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class BulkUnsubscribeChannelsUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(channelUsernames: List<String>) {
        repository.bulkUnsubscribe(channelUsernames)
    }
}
