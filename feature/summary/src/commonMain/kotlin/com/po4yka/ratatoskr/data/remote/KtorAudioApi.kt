package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.GenerateAudioResponseDto
import com.po4yka.ratatoskr.util.retry.RetryPolicy
import com.po4yka.ratatoskr.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.koin.core.annotation.Single

@Single(binds = [AudioApi::class])
class KtorAudioApi(private val client: HttpClient) : AudioApi {
    // No retry for generate - triggers server-side processing
    override suspend fun generateAudio(
        summaryId: Long,
        sourceField: String,
    ): ApiResponseDto<GenerateAudioResponseDto> {
        return client.post("v1/summaries/$summaryId/audio") {
            parameter("source_field", sourceField)
        }.body()
    }

    override suspend fun getAudioBytes(summaryId: Long): ByteArray =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/summaries/$summaryId/audio").body()
        }
}
