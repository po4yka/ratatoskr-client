package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDialog

// TODO: callers should migrate to FrostDialog directly; AppDialog is a transitional shim for Phase C migration.
@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable RowScope.() -> Unit)? = null,
    confirmButton: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val hasActions = dismissButton != null || confirmButton != null
    FrostDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = modifier,
        actions =
            if (hasActions) {
                {
                    dismissButton?.invoke(this)
                    confirmButton?.invoke(this)
                }
            } else {
                null
            },
        content = content,
    )
}
