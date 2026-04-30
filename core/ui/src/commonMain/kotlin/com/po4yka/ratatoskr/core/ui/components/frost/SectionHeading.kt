package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDivider
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost section heading: monoEmph uppercase with hairline bottom border.
 *
 * Accessibility: applies [heading] semantics. Implements DESIGN.md § Components — SectionHeading.
 */
@Composable
fun SectionHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(modifier.semantics { heading() }) {
        FrostText(
            text = text.uppercase(),
            style = AppTheme.frostType.monoEmph,
            color = AppTheme.frostColors.ink,
            modifier = Modifier.padding(bottom = AppTheme.spacing.cell),
        )
        FrostDivider(alpha = AppTheme.border.separatorAlpha)
    }
}

@Preview
@Composable
private fun SectionHeadingPreview() {
    RatatoskrTheme {
        SectionHeading(text = "System Collections")
    }
}
