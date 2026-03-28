package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.StatsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface StatsComponent {
    val viewModel: StatsViewModel
}

class DefaultStatsComponent(
    componentContext: ComponentContext,
) : StatsComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: StatsViewModel = retainedInstance { get() }
}
