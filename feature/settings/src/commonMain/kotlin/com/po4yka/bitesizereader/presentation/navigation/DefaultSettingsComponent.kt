package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> SettingsViewModel,
    private val onDigest: () -> Unit = {},
) : SettingsComponent, ComponentContext by componentContext {
    override val viewModel: SettingsViewModel = retainedInstance { viewModelFactory() }

    override fun onDigestClicked() {
        onDigest()
    }
}
