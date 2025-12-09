package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SummaryDetailComponent {
    val viewModel: SummaryDetailViewModel

    fun onBackClicked()
}

class DefaultSummaryDetailComponent(
    private val summaryId: String,
    private val onBack: () -> Unit,
) : SummaryDetailComponent, KoinComponent {
    override val viewModel: SummaryDetailViewModel by inject()

    init {
        viewModel.loadSummary(summaryId)
    }

    override fun onBackClicked() {
        onBack()
    }
}
