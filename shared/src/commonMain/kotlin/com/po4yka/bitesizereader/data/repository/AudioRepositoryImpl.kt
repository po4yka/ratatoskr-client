package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.AudioApi
import com.po4yka.bitesizereader.domain.model.AudioPlaybackState
import com.po4yka.bitesizereader.domain.model.AudioStatus
import com.po4yka.bitesizereader.domain.repository.AudioRepository
import org.koin.core.annotation.Single

@Single(binds = [AudioRepository::class])
class AudioRepositoryImpl(
    private val api: AudioApi,
) : AudioRepository {
    override suspend fun generateAudio(
        summaryId: Long,
        sourceField: String,
    ): Result<AudioPlaybackState> =
        runCatching {
            val response = api.generateAudio(summaryId, sourceField)
            val data = response.data ?: error("No audio generation data in response")
            AudioPlaybackState(
                summaryId = summaryId,
                status =
                    when (data.status) {
                        "completed" -> AudioStatus.IDLE
                        "queued", "processing" -> AudioStatus.GENERATING
                        "failed" -> AudioStatus.ERROR
                        else -> AudioStatus.IDLE
                    },
                error = data.error,
            )
        }

    override suspend fun getAudioBytes(summaryId: Long): Result<ByteArray> =
        runCatching {
            api.getAudioBytes(summaryId)
        }
}
