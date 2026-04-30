@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIndication
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost bracketed icon button: 40dp square box with hairline border and centred content slot.
 *
 * Implements DESIGN.md § Components — BracketIconButton, 0dp radius, hairline border.
 */
@Composable
fun BracketIconButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val ink = AppTheme.frostColors.ink
    val separatorAlpha = AppTheme.border.separatorAlpha
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .size(40.dp)
                .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    indication = FrostIndication,
                    interactionSource = interactionSource,
                    onClickLabel = contentDescription,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview
@Composable
private fun BracketIconButtonPreview() {
    RatatoskrTheme {
        BracketIconButton(onClick = {}, contentDescription = "Menu") {
            FrostText("≡")
        }
    }
}
