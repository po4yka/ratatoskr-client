package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.AudioPlaybackState
import com.po4yka.ratatoskr.domain.repository.AudioRepository
import org.koin.core.annotation.Factory

@Factory
class GenerateAudioUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(
        summaryId: String,
        sourceField: String = "summary_1000",
    ): AudioPlaybackState {
        return repository.generateAudio(summaryId, sourceField)
    }
}
