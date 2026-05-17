package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.ratatoskr.presentation.viewmodel.SearchViewModel
import com.po4yka.ratatoskr.navigation.ScreenComponent

interface SearchComponent : ScreenComponent {
    val viewModel: SearchViewModel

    fun onSummaryClicked(id: String)
}

class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> SearchViewModel,
    private val onSummarySelected: (String) -> Unit,
) : SearchComponent, ComponentContext by componentContext {
    override val viewModel: SearchViewModel = retainedInstance { viewModelFactory() }

    override fun onSummaryClicked(id: String) {
        onSummarySelected(id)
    }
}
