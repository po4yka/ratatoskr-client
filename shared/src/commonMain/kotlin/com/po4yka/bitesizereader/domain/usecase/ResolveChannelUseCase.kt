package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class ResolveChannelUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(channelUsername: String): ResolveChannelResponseDto {
        return repository.resolveChannel(channelUsername)
    }
}
