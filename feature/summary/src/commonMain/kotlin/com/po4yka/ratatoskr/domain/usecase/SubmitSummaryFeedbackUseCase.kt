package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.FeedbackIssue
import com.po4yka.ratatoskr.domain.model.FeedbackRating
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class SubmitSummaryFeedbackUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(
        summaryId: String,
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
    ) {
        repository.submitFeedback(summaryId, rating, issues, comment)
    }
}
