package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class BulkUnsubscribeChannelsUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(channelUsernames: List<String>) {
        repository.bulkUnsubscribe(channelUsernames)
    }
}
