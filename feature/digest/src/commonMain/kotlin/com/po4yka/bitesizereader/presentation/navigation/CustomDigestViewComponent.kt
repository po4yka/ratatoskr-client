package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestViewViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface CustomDigestViewComponent {
    val viewModel: CustomDigestViewViewModel
    val digestId: String

    fun onBackClicked()
}

class DefaultCustomDigestViewComponent(
    componentContext: ComponentContext,
    override val digestId: String,
    private val onBack: () -> Unit,
) : CustomDigestViewComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: CustomDigestViewViewModel =
        retainedInstance {
            get<CustomDigestViewViewModel>().also { it.loadDigest(digestId) }
        }

    override fun onBackClicked() {
        onBack()
    }
}
