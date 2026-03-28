package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.domain.model.FeedbackRating
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.collections_cancel
import bitesizereader.composeapp.generated.resources.feedback_dialog_comment_label
import bitesizereader.composeapp.generated.resources.feedback_dialog_prompt
import bitesizereader.composeapp.generated.resources.feedback_dialog_submit
import bitesizereader.composeapp.generated.resources.feedback_dialog_submitting
import bitesizereader.composeapp.generated.resources.feedback_dialog_title
import bitesizereader.composeapp.generated.resources.feedback_issue_inaccurate
import bitesizereader.composeapp.generated.resources.feedback_issue_missing_context
import bitesizereader.composeapp.generated.resources.feedback_issue_other
import bitesizereader.composeapp.generated.resources.feedback_issue_outdated
import bitesizereader.composeapp.generated.resources.feedback_issue_poorly_written
import bitesizereader.composeapp.generated.resources.feedback_issue_too_short
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

    CarbonDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = stringResource(Res.string.feedback_dialog_title),
        dismissButton = {
            Button(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                isEnabled = !isSubmitting,
                buttonType = ButtonType.Ghost,
            )
        },
        confirmButton = {
            Button(
                label = stringResource(Res.string.feedback_dialog_submit),
                onClick = {
                    val commentValue = comment.trim().ifEmpty { null }
                    onSubmit(rating, checkedIssues.toList(), commentValue)
                },
                isEnabled = !isSubmitting,
                buttonType = ButtonType.Primary,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(Res.string.feedback_dialog_prompt),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
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
                    CarbonCheckbox(
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
                    Text(
                        text = issue.displayName(),
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textPrimary,
                        modifier = Modifier.padding(start = Spacing.xs),
                    )
                }
            }
            CarbonTextArea(
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
                    SmallLoading()
                    Text(
                        text = stringResource(Res.string.feedback_dialog_submitting),
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textSecondary,
                    )
                }
            }
        }
    }
}
