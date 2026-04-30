package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost-native scaffold: header + weighted content area + footer.
 *
 * Backed by [Column] + page background. No Material 3 Scaffold or TopAppBar.
 * Implements DESIGN.md § Components — brutalist layout, no elevation.
 */
@Composable
fun FrostScaffold(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(modifier.fillMaxSize().background(AppTheme.frostColors.page)) {
        header()
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        footer()
    }
}

@Preview
@Composable
private fun FrostScaffoldPreview() {
    RatatoskrTheme {
        FrostScaffold(
            header = { FrostText("HEADER") },
            footer = { FrostText("FOOTER") },
        ) {
            FrostText("Content")
        }
    }
}
