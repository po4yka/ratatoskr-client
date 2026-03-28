package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onDigest: () -> Unit = {},
) : SettingsComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: SettingsViewModel = retainedInstance { get() }

    override fun onDigestClicked() {
        onDigest()
    }
}
