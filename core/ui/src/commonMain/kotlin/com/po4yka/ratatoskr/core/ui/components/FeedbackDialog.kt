package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.FeedbackIssue
import com.po4yka.ratatoskr.domain.model.FeedbackRating
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.collections_cancel
import ratatoskr.core.ui.generated.resources.feedback_dialog_comment_label
import ratatoskr.core.ui.generated.resources.feedback_dialog_prompt
import ratatoskr.core.ui.generated.resources.feedback_dialog_submit
import ratatoskr.core.ui.generated.resources.feedback_dialog_submitting
import ratatoskr.core.ui.generated.resources.feedback_dialog_title
import ratatoskr.core.ui.generated.resources.feedback_issue_inaccurate
import ratatoskr.core.ui.generated.resources.feedback_issue_missing_context
import ratatoskr.core.ui.generated.resources.feedback_issue_other
import ratatoskr.core.ui.generated.resources.feedback_issue_outdated
import ratatoskr.core.ui.generated.resources.feedback_issue_poorly_written
import ratatoskr.core.ui.generated.resources.feedback_issue_too_short
import org.jetbrains.compose.resources.stringResource

@Composable
private fun FeedbackIssue.displayName(): String =
    when (this) {
        FeedbackIssue.TOO_SHORT -> stringResource(Res.string.feedback_issue_too_short)
        FeedbackIssue.INACCURATE -> stringResource(Res.string.feedback_issue_inaccurate)
        FeedbackIssue.MISSING_CONTEXT -> stringResource(Res.string.feedback_issue_missing_context)
        FeedbackIssue.OUTDATED -> stringResource(Res.string.feedback_issue_outdated)
        FeedbackIssue.POORLY_WRITTEN -> stringResource(Res.string.feedback_issue_poorly_written)
        FeedbackIssue.OTHER -> stringResource(Res.string.feedback_issue_other)
    }

@Suppress("FunctionNaming")
@Composable
fun FeedbackDialog(
    rating: FeedbackRating,
    isSubmitting: Boolean,
    onSubmit: (rating: FeedbackRating, issues: List<FeedbackIssue>, comment: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var checkedIssues by remember { mutableStateOf(setOf<FeedbackIssue>()) }
    var comment by remember { mutableStateOf("") }

    AppDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = stringResource(Res.string.feedback_dialog_title),
        dismissButton = {
            BracketButton(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                enabled = !isSubmitting,
            )
        },
        confirmButton = {
            BracketButton(
                label = stringResource(Res.string.feedback_dialog_submit),
                onClick = {
                    val commentValue = comment.trim().ifEmpty { null }
                    onSubmit(rating, checkedIssues.toList(), commentValue)
                },
                enabled = !isSubmitting,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            FrostText(
                text = stringResource(Res.string.feedback_dialog_prompt),
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
            FeedbackIssue.entries.forEach { issue ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isSubmitting) {
                                checkedIssues =
                                    if (issue in checkedIssues) {
                                        checkedIssues - issue
                                    } else {
                                        checkedIssues + issue
                                    }
                            },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppCheckbox(
                        checked = issue in checkedIssues,
                        onCheckedChange = { checked ->
                            checkedIssues =
                                if (checked) {
                                    checkedIssues + issue
                                } else {
                                    checkedIssues - issue
                                }
                        },
                        enabled = !isSubmitting,
                    )
                    FrostText(
                        text = issue.displayName(),
                        style = AppTheme.frostType.monoBody,
                        color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.active),
                        modifier = Modifier.padding(start = Spacing.xs),
                    )
                }
            }
            TextArea(
                label = stringResource(Res.string.feedback_dialog_comment_label),
                value = comment,
                onValueChange = { comment = it },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )
            if (isSubmitting) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    AppSmallSpinner()
                    FrostText(
                        text = stringResource(Res.string.feedback_dialog_submitting),
                        style = AppTheme.frostType.monoBody,
                        color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }
            }
        }
    }
}
