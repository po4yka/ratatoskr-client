package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.presentation.viewmodel.SummaryDetailViewModel

interface SummaryDetailComponent {
    val viewModel: SummaryDetailViewModel
    val summaryId: String

    fun onBackClicked()

    fun rateSummary(rating: FeedbackRating)

    fun submitDetailedFeedback(
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    )

    fun dismissFeedbackDialog()

    fun openResummarizeConfirmDialog()

    fun dismissResummarizeConfirmDialog()

    fun resummarize()
}

class DefaultSummaryDetailComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> SummaryDetailViewModel,
    override val summaryId: String,
    private val onBack: () -> Unit,
) : SummaryDetailComponent, ComponentContext by componentContext {
    override val viewModel: SummaryDetailViewModel =
        retainedInstance {
            viewModelFactory().also { it.loadSummary(summaryId) }
        }

    override fun onBackClicked() {
        onBack()
    }

    override fun rateSummary(rating: FeedbackRating) {
        viewModel.rateSummary(rating)
    }

    override fun submitDetailedFeedback(
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    ) {
        viewModel.submitDetailedFeedback(rating, issues, comment)
    }

    override fun dismissFeedbackDialog() {
        viewModel.dismissFeedbackDialog()
    }

    override fun openResummarizeConfirmDialog() {
        viewModel.openResummarizeConfirmDialog()
    }

    override fun dismissResummarizeConfirmDialog() {
        viewModel.dismissResummarizeConfirmDialog()
    }

    override fun resummarize() {
        viewModel.resummarize()
    }
}
