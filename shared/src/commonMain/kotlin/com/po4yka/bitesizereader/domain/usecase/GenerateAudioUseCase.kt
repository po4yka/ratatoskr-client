package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.AudioPlaybackState
import com.po4yka.bitesizereader.domain.repository.AudioRepository
import org.koin.core.annotation.Factory

@Factory
class GenerateAudioUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(
        summaryId: Long,
        sourceField: String = "summary_1000",
    ): Result<AudioPlaybackState> {
        return repository.generateAudio(summaryId, sourceField)
    }
}
