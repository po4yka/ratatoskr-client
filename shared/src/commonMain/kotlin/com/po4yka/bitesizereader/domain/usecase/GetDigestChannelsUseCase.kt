package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.DigestRepository
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Factory

@Factory
class GetDigestChannelsUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): JsonObject {
        return repository.getChannels()
    }
}
