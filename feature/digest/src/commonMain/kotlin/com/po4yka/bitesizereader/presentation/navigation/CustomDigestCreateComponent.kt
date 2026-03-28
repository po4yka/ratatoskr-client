package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestCreateViewModel

interface CustomDigestCreateComponent {
    val viewModel: CustomDigestCreateViewModel

    fun onBackClicked()

    fun onDigestCreated(digestId: String)
}

class DefaultCustomDigestCreateComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> CustomDigestCreateViewModel,
    private val onBack: () -> Unit,
    private val onDigestCreated: (String) -> Unit,
) : CustomDigestCreateComponent, ComponentContext by componentContext {
    override val viewModel: CustomDigestCreateViewModel = retainedInstance { viewModelFactory() }

    override fun onBackClicked() {
        onBack()
    }

    override fun onDigestCreated(digestId: String) {
        onDigestCreated.invoke(digestId)
    }
}
