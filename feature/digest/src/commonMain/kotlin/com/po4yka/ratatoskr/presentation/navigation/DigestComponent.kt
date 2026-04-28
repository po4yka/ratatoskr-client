package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.ratatoskr.presentation.viewmodel.DigestViewModel

interface DigestComponent {
    val viewModel: DigestViewModel

    fun onBackClicked()
}

class DefaultDigestComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> DigestViewModel,
    private val onBack: () -> Unit,
) : DigestComponent, ComponentContext by componentContext {
    override val viewModel: DigestViewModel = retainedInstance { viewModelFactory() }

    override fun onBackClicked() {
        onBack()
    }
}
