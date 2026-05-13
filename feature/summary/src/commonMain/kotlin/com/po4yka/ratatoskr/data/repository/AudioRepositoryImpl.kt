package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.SummariesApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField
import com.po4yka.ratatoskr.data.remote.dto.GenerateAudioResponseDto
import com.po4yka.ratatoskr.domain.model.AudioPlaybackState
import com.po4yka.ratatoskr.domain.model.AudioStatus
import com.po4yka.ratatoskr.domain.repository.AudioRepository
import io.ktor.client.call.body
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

private val lenientJson = Json { ignoreUnknownKeys = true }

@Single(binds = [AudioRepository::class])
class AudioRepositoryImpl : AudioRepository {
    override suspend fun generateAudio(
        summaryId: String,
        sourceField: String,
    ): AudioPlaybackState {
        val remoteId = summaryId.toLong()
        val sourceFieldEnum = when (sourceField) {
            "summary_250" -> GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField.SUMMARY_250
            "tldr" -> GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField.TLDR
            else -> GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField.SUMMARY_1000
        }
        val jsonElement = SummariesApi.generateSummaryAudioV1SummariesSummaryIdAudioPost(
            summaryId = remoteId,
            sourceField = sourceFieldEnum,
        ).unwrap()
        val data = lenientJson.decodeFromJsonElement(GenerateAudioResponseDto.serializer(), jsonElement)
        return AudioPlaybackState(
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

    override suspend fun getAudioBytes(summaryId: String): ByteArray {
        val remoteId = summaryId.toLong()
        val httpResponse = SummariesApi.getSummaryAudioV1SummariesSummaryIdAudioGet(remoteId).unwrap()
        return httpResponse.body()
    }
}
