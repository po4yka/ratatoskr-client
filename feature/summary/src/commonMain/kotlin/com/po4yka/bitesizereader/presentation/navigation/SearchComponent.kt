package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SearchViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface SearchComponent {
    val viewModel: SearchViewModel

    fun onSummaryClicked(id: String)
}

class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val onSummarySelected: (String) -> Unit,
) : SearchComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: SearchViewModel = retainedInstance { get() }

    override fun onSummaryClicked(id: String) {
        onSummarySelected(id)
    }
}
