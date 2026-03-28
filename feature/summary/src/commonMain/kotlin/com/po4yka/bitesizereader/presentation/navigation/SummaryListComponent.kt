package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.RecommendationsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalController
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryListViewModel

interface SummaryListComponent {
    val viewModel: SummaryListViewModel
    val recommendationsViewModel: RecommendationsViewModel
    val readingGoalController: ReadingGoalController

    fun onSummaryClicked(id: String)

    fun onSubmitUrlClicked()

    fun onCreateDigestClicked()
}

class DefaultSummaryListComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> SummaryListViewModel,
    private val recommendationsViewModelFactory: () -> RecommendationsViewModel,
    private val readingGoalControllerFactory: () -> ReadingGoalController,
    private val onSummarySelected: (String) -> Unit,
    private val onSubmitUrl: () -> Unit,
    private val onCreateDigest: () -> Unit,
) : SummaryListComponent, ComponentContext by componentContext {
    override val viewModel: SummaryListViewModel = retainedInstance { viewModelFactory() }
    override val recommendationsViewModel: RecommendationsViewModel =
        retainedInstance { recommendationsViewModelFactory() }
    override val readingGoalController: ReadingGoalController =
        retainedInstance { readingGoalControllerFactory() }

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
