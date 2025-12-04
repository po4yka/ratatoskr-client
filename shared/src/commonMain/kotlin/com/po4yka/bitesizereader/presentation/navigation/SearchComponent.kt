package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SearchComponent {
    val viewModel: SearchViewModel
    fun onSummaryClicked(id: String)
}

class DefaultSearchComponent(
    private val onSummarySelected: (String) -> Unit
) : SearchComponent, KoinComponent {
    override val viewModel: SearchViewModel by inject()

    override fun onSummaryClicked(id: String) {
        onSummarySelected(id)
    }
}
