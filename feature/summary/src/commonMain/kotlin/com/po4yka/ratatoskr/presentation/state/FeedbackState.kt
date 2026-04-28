package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.ProcessingStage
import com.po4yka.ratatoskr.domain.model.SummaryFeedback

data class FeedbackState(
    val feedback: SummaryFeedback? = null,
    val showFeedbackDialog: Boolean = false,
    val isSubmittingFeedback: Boolean = false,
    val isResummarizing: Boolean = false,
    val resummarizeProgress: Float = 0f,
    val resummarizeStage: ProcessingStage = ProcessingStage.UNSPECIFIED,
    val resummarizeError: String? = null,
    val showResummarizeConfirmDialog: Boolean = false,
)
