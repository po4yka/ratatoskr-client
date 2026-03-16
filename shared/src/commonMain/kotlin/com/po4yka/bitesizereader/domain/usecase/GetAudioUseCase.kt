package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.AudioRepository
import org.koin.core.annotation.Factory

@Factory
class GetAudioUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(summaryId: Long): Result<ByteArray> {
        return repository.getAudioBytes(summaryId)
    }
}
