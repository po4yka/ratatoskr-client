package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.AudioPlaybackState
import com.po4yka.ratatoskr.domain.model.AudioStatus
import com.po4yka.ratatoskr.domain.usecase.GenerateAudioUseCase
import com.po4yka.ratatoskr.domain.usecase.GetAudioUseCase
import com.po4yka.ratatoskr.util.audio.AudioPlayer
import com.po4yka.ratatoskr.util.error.toAppError
import com.po4yka.ratatoskr.util.error.userMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class AudioDelegate(
    private val generateAudioUseCase: GenerateAudioUseCase,
    private val getAudioUseCase: GetAudioUseCase,
    private val audioPlayer: AudioPlayer,
) {
    @Suppress("TooGenericExceptionCaught")
    fun generateAndPlayAudio(
        summaryId: Long,
        sourceField: String,
        scope: CoroutineScope,
        currentState: () -> AudioPlaybackState?,
        onState: (AudioPlaybackState?) -> Unit,
    ) {
        onState(AudioPlaybackState(summaryId = summaryId, status = AudioStatus.GENERATING))
        scope.launch {
            try {
                val genResult = generateAudioUseCase(summaryId, sourceField)
                genResult.getOrThrow()
                onState(currentState()?.copy(status = AudioStatus.LOADING))
                val audioBytes = getAudioUseCase(summaryId).getOrThrow()
                audioPlayer.playFromBytes(audioBytes)
                onState(
                    currentState()?.copy(
                        status = AudioStatus.PLAYING,
                        durationMs = audioPlayer.durationMs,
                    ),
                )
            } catch (e: Exception) {
                onState(
                    currentState()?.copy(
                        status = AudioStatus.ERROR,
                        error = e.toAppError().userMessage(),
                    ),
                )
            }
        }
    }

    fun toggleAudioPlayback(
        currentState: () -> AudioPlaybackState?,
        onState: (AudioPlaybackState?) -> Unit,
    ) {
        val audio = currentState() ?: return
        when (audio.status) {
            AudioStatus.PLAYING -> {
                audioPlayer.pause()
                onState(audio.copy(status = AudioStatus.PAUSED))
            }
            AudioStatus.PAUSED -> {
                audioPlayer.resume()
                onState(audio.copy(status = AudioStatus.PLAYING))
            }
            else -> { /* no-op for other states */ }
        }
    }

    fun stopAudio(onState: (AudioPlaybackState?) -> Unit) {
        audioPlayer.stop()
        onState(null)
    }
}
