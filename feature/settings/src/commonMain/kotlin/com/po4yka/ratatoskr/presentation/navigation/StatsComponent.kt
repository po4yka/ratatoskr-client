package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.ratatoskr.presentation.viewmodel.StatsViewModel

interface StatsComponent {
    val viewModel: StatsViewModel
}

class DefaultStatsComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> StatsViewModel,
) : StatsComponent, ComponentContext by componentContext {
    override val viewModel: StatsViewModel = retainedInstance { viewModelFactory() }
}
