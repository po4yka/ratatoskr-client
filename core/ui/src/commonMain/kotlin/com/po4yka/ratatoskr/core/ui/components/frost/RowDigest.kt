package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDivider
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost single-row digest: uppercase label (secondary alpha) + body value + optional trailing action.
 *
 * Hairline bottom border at rowDividerAlpha. Implements DESIGN.md § Components — Row/Digest.
 */
@Composable
fun RowDigest(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    val ink = AppTheme.frostColors.ink

    androidx.compose.foundation.layout.Column(modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spacing.cell),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gapInline),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FrostText(
                text = label.uppercase(),
                style = AppTheme.frostType.monoSm,
                color = ink.copy(alpha = AppTheme.alpha.secondary),
                modifier = Modifier.weight(1f),
            )
            FrostText(
                text = value,
                style = AppTheme.frostType.monoBody,
                color = ink,
            )
            action?.invoke()
        }
        FrostDivider(alpha = AppTheme.border.rowDividerAlpha)
    }
}

@Preview
@Composable
private fun RowDigestPreview() {
    RatatoskrTheme {
        RowDigest(label = "Articles read", value = "42")
    }
}
