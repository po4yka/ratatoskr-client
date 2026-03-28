package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface SummaryListComponent {
    val viewModel: SummaryListViewModel

    fun onSummaryClicked(id: String)

    fun onSubmitUrlClicked()

    fun onCreateDigestClicked()
}

class DefaultSummaryListComponent(
    componentContext: ComponentContext,
    private val onSummarySelected: (String) -> Unit,
    private val onSubmitUrl: () -> Unit,
    private val onCreateDigest: () -> Unit,
) : SummaryListComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: SummaryListViewModel = retainedInstance { get() }

    override fun onSummaryClicked(id: String) {
        onSummarySelected(id)
    }

    override fun onSubmitUrlClicked() {
        onSubmitUrl()
    }

    override fun onCreateDigestClicked() {
        onCreateDigest()
    }
}
