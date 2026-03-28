package com.po4yka.bitesizereader.presentation.viewmodel

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.po4yka.bitesizereader.presentation.state.ReadingGoalState
import kotlinx.coroutines.flow.StateFlow

interface ReadingGoalController : InstanceKeeper.Instance {
    val state: StateFlow<ReadingGoalState>

    fun setDailyTarget(minutes: Int)

    fun toggleEnabled()
}
