package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultSettingsComponent(
    componentContext: ComponentContext,
) : SettingsComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: SettingsViewModel by inject()
}
