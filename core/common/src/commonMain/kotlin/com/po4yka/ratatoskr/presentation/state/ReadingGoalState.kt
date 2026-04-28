package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.ReadingGoalProgress

data class ReadingGoalState(
    val goalProgress: ReadingGoalProgress? = null,
    val isEditing: Boolean = false,
    val editTarget: Int = 15,
)
