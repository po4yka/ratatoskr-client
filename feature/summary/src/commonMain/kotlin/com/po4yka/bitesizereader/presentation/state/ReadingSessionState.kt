package com.po4yka.bitesizereader.presentation.state

data class ReadingSessionState(
    val isSessionPaused: Boolean = false,
    val currentSessionDurationSec: Int = 0,
)
