package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.po4yka.bitesizereader.domain.model.FeedbackIssue
import com.po4yka.bitesizereader.ui.theme.Spacing

private fun FeedbackIssue.displayName(): String =
    when (this) {
        FeedbackIssue.TOO_SHORT -> "Too short"
        FeedbackIssue.INACCURATE -> "Inaccurate"
        FeedbackIssue.MISSING_CONTEXT -> "Missing context"
        FeedbackIssue.OUTDATED -> "Outdated"
        FeedbackIssue.POORLY_WRITTEN -> "Poorly written"
        FeedbackIssue.OTHER -> "Other"
    }

@Suppress("FunctionNaming")
@Composable
fun FeedbackDialog(
    isSubmitting: Boolean,
    onSubmit: (issues: List<FeedbackIssue>, comment: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var checkedIssues by remember { mutableStateOf(setOf<FeedbackIssue>()) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        containerColor = Carbon.theme.layer01,
        title = {
            Text(
                text = "Report Feedback",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "What was wrong with this summary?",
                    style = Carbon.typography.bodyCompact01,
                    color = Carbon.theme.textSecondary,
                )
                FeedbackIssue.entries.forEach { issue ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
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
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = {
                        Text(
                            text = "Additional comments (optional)",
                            style = Carbon.typography.label01,
                        )
                    },
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
                            text = "Submitting...",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                label = "Submit",
                onClick = {
                    val commentValue = comment.trim().ifEmpty { null }
                    onSubmit(checkedIssues.toList(), commentValue)
                },
                isEnabled = !isSubmitting,
                buttonType = ButtonType.Primary,
            )
        },
        dismissButton = {
            Button(
                label = "Cancel",
                onClick = onDismiss,
                isEnabled = !isSubmitting,
                buttonType = ButtonType.Ghost,
            )
        },
    )
}
