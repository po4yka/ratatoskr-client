package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.GenerateAudioResponseDto

/**
 * TTS audio API for summary audio generation and playback.
 */
interface AudioApi {
    /**
     * Trigger audio generation for a summary.
     *
     * @param summaryId Summary ID
     * @param sourceField Which summary field to convert: "summary_250", "summary_1000", or "tldr"
     */
    suspend fun generateAudio(
        summaryId: Long,
        sourceField: String = "summary_1000",
    ): ApiResponseDto<GenerateAudioResponseDto>

    /**
     * Download the generated MP3 audio as raw bytes.
     *
     * @param summaryId Summary ID
     * @return Raw MP3 bytes
     */
    suspend fun getAudioBytes(summaryId: Long): ByteArray
}
