package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SubmitURLComponent {
    val viewModel: SubmitURLViewModel

    fun onBackClicked()
}

class DefaultSubmitURLComponent(
    private val onBack: () -> Unit,
) : SubmitURLComponent, KoinComponent {
    override val viewModel: SubmitURLViewModel by inject()

    override fun onBackClicked() {
        onBack()
    }
}
