package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SubmitURLViewModel

interface SubmitURLComponent {
    val viewModel: SubmitURLViewModel

    fun onBackClicked()

    fun onViewExistingSummary(summaryId: String)
}

class DefaultSubmitURLComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> SubmitURLViewModel,
    private val prefilledUrl: String?,
    private val onBack: () -> Unit,
    private val onNavigateToSummary: (String) -> Unit,
) : SubmitURLComponent, ComponentContext by componentContext {
    override val viewModel: SubmitURLViewModel = retainedInstance { viewModelFactory() }

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
