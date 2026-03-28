package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.ResolvedChannel
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class ResolveChannelUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(channelUsername: String): ResolvedChannel {
        return repository.resolveChannel(channelUsername)
    }
}
