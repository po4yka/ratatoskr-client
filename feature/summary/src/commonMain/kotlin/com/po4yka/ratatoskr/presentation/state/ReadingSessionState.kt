package com.po4yka.ratatoskr.presentation.state

data class ReadingSessionState(
    val isSessionPaused: Boolean = false,
    val currentSessionDurationSec: Int = 0,
)
