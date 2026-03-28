package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.DigestViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface DigestComponent {
    val viewModel: DigestViewModel

    fun onBackClicked()
}

class DefaultDigestComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
) : DigestComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: DigestViewModel = retainedInstance { get() }

    override fun onBackClicked() {
        onBack()
    }
}
