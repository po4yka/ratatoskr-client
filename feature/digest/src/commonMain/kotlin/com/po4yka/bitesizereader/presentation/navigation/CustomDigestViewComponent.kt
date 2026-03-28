package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestViewViewModel

interface CustomDigestViewComponent {
    val viewModel: CustomDigestViewViewModel
    val digestId: String

    fun onBackClicked()
}

class DefaultCustomDigestViewComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> CustomDigestViewViewModel,
    override val digestId: String,
    private val onBack: () -> Unit,
) : CustomDigestViewComponent, ComponentContext by componentContext {
    override val viewModel: CustomDigestViewViewModel =
        retainedInstance {
            viewModelFactory().also { it.loadDigest(digestId) }
        }

    override fun onBackClicked() {
        onBack()
    }
}
