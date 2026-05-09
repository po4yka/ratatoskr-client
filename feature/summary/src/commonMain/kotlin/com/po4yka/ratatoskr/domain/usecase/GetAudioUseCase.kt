package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.AudioRepository
import org.koin.core.annotation.Factory

@Factory
class GetAudioUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(summaryId: String): ByteArray {
        return repository.getAudioBytes(summaryId)
    }
}
