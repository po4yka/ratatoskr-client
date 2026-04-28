package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.ResolvedChannel
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class ResolveChannelUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(channelUsername: String): ResolvedChannel {
        return repository.resolveChannel(channelUsername)
    }
}
