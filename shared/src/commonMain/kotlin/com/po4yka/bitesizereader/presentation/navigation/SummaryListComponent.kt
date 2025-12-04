package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SummaryListComponent {
    val viewModel: SummaryListViewModel
    fun onSummaryClicked(id: String)
}

class DefaultSummaryListComponent(
    private val onSummarySelected: (String) -> Unit
) : SummaryListComponent, KoinComponent {
    override val viewModel: SummaryListViewModel by inject()

    override fun onSummaryClicked(id: String) {
        onSummarySelected(id)
    }
}
