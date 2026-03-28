package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestCreateViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface CustomDigestCreateComponent {
    val viewModel: CustomDigestCreateViewModel

    fun onBackClicked()

    fun onDigestCreated(digestId: String)
}

class DefaultCustomDigestCreateComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
    private val onDigestCreated: (String) -> Unit,
) : CustomDigestCreateComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: CustomDigestCreateViewModel = retainedInstance { get() }

    override fun onBackClicked() {
        onBack()
    }

    override fun onDigestCreated(digestId: String) {
        onDigestCreated.invoke(digestId)
    }
}
