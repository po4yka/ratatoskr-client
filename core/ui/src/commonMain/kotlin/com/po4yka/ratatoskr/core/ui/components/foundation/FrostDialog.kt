package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost-native dialog backed by [androidx.compose.ui.window.Dialog] (no Material 3).
 *
 * Body is a [Column] with hairline border, no corner radius, page background, padPage padding.
 * Title rendered in monoEmph. Implements DESIGN.md § Components — brutalist slab dialogs.
 */
@Composable
fun FrostDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .background(AppTheme.frostColors.page)
                    .border(
                        AppTheme.border.hairline,
                        AppTheme.frostColors.ink.copy(alpha = AppTheme.border.separatorAlpha),
                    )
                    .padding(AppTheme.spacing.padPage),
        ) {
            FrostText(
                text = title,
                style = AppTheme.frostType.monoEmph,
                modifier = Modifier.padding(bottom = AppTheme.spacing.line),
            )
            content()
            if (actions != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.spacing.line),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions()
                }
            }
        }
    }
}

@Preview
@Composable
private fun FrostDialogPreview() {
    RatatoskrTheme {
        FrostDialog(
            onDismissRequest = {},
            title = "CONFIRM",
            actions = {
                FrostText("[ OK ]")
            },
        ) {
            FrostText("Dialog body content here.")
        }
    }
}
