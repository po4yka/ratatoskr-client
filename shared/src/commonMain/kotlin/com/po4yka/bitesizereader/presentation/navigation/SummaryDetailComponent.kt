package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface SummaryDetailComponent {
    val viewModel: SummaryDetailViewModel
    val summaryId: String

    fun onBackClicked()
}

class DefaultSummaryDetailComponent(
    componentContext: ComponentContext,
    override val summaryId: String,
    private val onBack: () -> Unit,
) : SummaryDetailComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: SummaryDetailViewModel =
        retainedInstance {
            get<SummaryDetailViewModel>().also { it.loadSummary(summaryId) }
        }

    override fun onBackClicked() {
        onBack()
    }
}
