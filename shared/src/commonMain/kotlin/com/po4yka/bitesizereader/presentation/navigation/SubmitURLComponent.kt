package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface SubmitURLComponent {
    val viewModel: SubmitURLViewModel

    fun onBackClicked()

    fun onViewExistingSummary(summaryId: String)
}

class DefaultSubmitURLComponent(
    componentContext: ComponentContext,
    private val prefilledUrl: String?,
    private val onBack: () -> Unit,
    private val onNavigateToSummary: (String) -> Unit,
) : SubmitURLComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: SubmitURLViewModel = retainedInstance { get() }

    init {
        if (!prefilledUrl.isNullOrBlank()) {
            viewModel.onUrlChanged(prefilledUrl)
        }
    }

    override fun onBackClicked() {
        onBack()
    }

    override fun onViewExistingSummary(summaryId: String) {
        onNavigateToSummary(summaryId)
    }
}
