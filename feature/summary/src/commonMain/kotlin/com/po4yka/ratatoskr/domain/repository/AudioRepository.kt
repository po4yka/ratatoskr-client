package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.AudioPlaybackState

interface AudioRepository {
    /** Trigger audio generation on the server. */
    suspend fun generateAudio(
        summaryId: Long,
        sourceField: String,
    ): Result<AudioPlaybackState>

    /** Download generated audio bytes. */
    suspend fun getAudioBytes(summaryId: Long): Result<ByteArray>
}
