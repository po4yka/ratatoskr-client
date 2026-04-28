package com.po4yka.ratatoskr.domain.model

data class AudioPlaybackState(
    val summaryId: Long = 0,
    val status: AudioStatus = AudioStatus.IDLE,
    val progress: Float = 0f,
    val durationMs: Long = 0,
    val currentPositionMs: Long = 0,
    val error: String? = null,
)

enum class AudioStatus {
    IDLE,
    GENERATING,
    LOADING,
    PLAYING,
    PAUSED,
    ERROR,
}
