package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.usecase.GetSummaryFeedbackUseCase
import com.po4yka.bitesizereader.domain.usecase.SubmitSummaryFeedbackUseCase
import com.po4yka.bitesizereader.presentation.state.FeedbackState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class FeedbackDelegate(
    private val submitSummaryFeedbackUseCase: SubmitSummaryFeedbackUseCase,
    private val getSummaryFeedbackUseCase: GetSummaryFeedbackUseCase,
    private val processingService: ProcessingService,
) {
    fun observeFeedback(
        summaryId: String,
        scope: CoroutineScope,
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
    ) {
        getSummaryFeedbackUseCase(summaryId)
            .onEach { feedback -> onState(currentState().copy(feedback = feedback)) }
            .launchIn(scope)
    }

    fun rateSummary(
        summaryId: String,
        rating: FeedbackRating,
        scope: CoroutineScope,
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
    ) {
        if (rating == FeedbackRating.UP) {
            scope.launch {
                try {
                    submitSummaryFeedbackUseCase(summaryId, rating, emptyList(), null)
                } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                    logger.warn(e) { "Failed to submit thumbs up feedback for $summaryId" }
                }
            }
        } else {
            onState(currentState().copy(showFeedbackDialog = true))
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun submitDetailedFeedback(
        summaryId: String,
        rating: FeedbackRating,
        issues: List<FeedbackIssue>,
        comment: String?,
        scope: CoroutineScope,
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isSubmittingFeedback = true))
            try {
                submitSummaryFeedbackUseCase(summaryId, rating, issues, comment)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to submit detailed feedback for $summaryId" }
            } finally {
                onState(currentState().copy(isSubmittingFeedback = false, showFeedbackDialog = false))
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun resummarize(
        sourceUrl: String,
        scope: CoroutineScope,
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
        onSummaryReload: (String) -> Unit,
    ) {
        if (currentState().isResummarizing) return
        dismissResummarizeConfirmDialog(currentState, onState)
        scope.launch {
            onState(
                currentState().copy(
                    isResummarizing = true,
                    resummarizeError = null,
                    resummarizeProgress = 0f,
                    resummarizeStage = ProcessingStage.UNSPECIFIED,
                ),
            )
            try {
                processingService.submitUrl(sourceUrl, forceRefresh = true).collect { update ->
                    onState(currentState().copy(resummarizeProgress = update.progress, resummarizeStage = update.stage))
                    if (update.stage == ProcessingStage.DONE) {
                        onSummaryReload(sourceUrl)
                        onState(currentState().copy(isResummarizing = false))
                        return@collect
                    }
                }
            } catch (e: Exception) {
                logger.warn { "Re-summarize failed: ${e.message}" }
                onState(
                    currentState().copy(
                        isResummarizing = false,
                        resummarizeError = e.message ?: "Re-summarization failed",
                    ),
                )
            }
        }
    }

    fun openResummarizeConfirmDialog(
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
    ) {
        onState(currentState().copy(showResummarizeConfirmDialog = true))
    }

    fun dismissResummarizeConfirmDialog(
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
    ) {
        onState(currentState().copy(showResummarizeConfirmDialog = false))
    }

    fun dismissFeedbackDialog(
        currentState: () -> FeedbackState,
        onState: (FeedbackState) -> Unit,
    ) {
        onState(currentState().copy(showFeedbackDialog = false))
    }
}
