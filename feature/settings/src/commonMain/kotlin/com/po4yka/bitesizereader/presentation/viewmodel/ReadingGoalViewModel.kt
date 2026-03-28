package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.GetReadingGoalProgressUseCase
import com.po4yka.bitesizereader.domain.usecase.RecalculateStreakUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateReadingGoalUseCase
import com.po4yka.bitesizereader.presentation.state.ReadingGoalState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReadingGoalViewModel(
    private val getReadingGoalProgressUseCase: GetReadingGoalProgressUseCase,
    private val updateReadingGoalUseCase: UpdateReadingGoalUseCase,
    private val recalculateStreakUseCase: RecalculateStreakUseCase,
) : BaseViewModel(), ReadingGoalController {
    private val _state = MutableStateFlow(ReadingGoalState())
    override val state: StateFlow<ReadingGoalState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getReadingGoalProgressUseCase().collect { progress ->
                _state.update { it.copy(goalProgress = progress, editTarget = progress.goal.dailyTargetMin) }
            }
        }
        viewModelScope.launch {
            recalculateStreakUseCase()
        }
    }

    override fun setDailyTarget(minutes: Int) {
        _state.update { it.copy(editTarget = minutes) }
        viewModelScope.launch {
            updateReadingGoalUseCase.setTarget(minutes)
        }
    }

    override fun toggleEnabled() {
        viewModelScope.launch {
            val current = _state.value.goalProgress?.goal?.isEnabled ?: false
            updateReadingGoalUseCase.setEnabled(!current)
        }
    }

    fun startEditing() {
        _state.update { it.copy(isEditing = true) }
    }

    fun stopEditing() {
        _state.update { it.copy(isEditing = false) }
    }
}
