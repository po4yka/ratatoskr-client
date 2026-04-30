@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme
import kotlinx.coroutines.delay

private val spinnerGlyphs = listOf("[/]", "[—]", "[\\]", "[|]")

/**
 * Frost ASCII spinner: cycles `[/]` `[—]` `[\]` `[|]` at the blinker rate.
 *
 * LiveRegion semantics for accessibility. Implements DESIGN.md § Components — FrostSpinner.
 * Replaces Material 3 CircularProgressIndicator — no graphics, pure monospace.
 */
@Composable
fun FrostSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) {
    var index by remember { mutableIntStateOf(0) }

    // Drive frame rate from blinker spec (FrostMotion.blinker is infinite repeatable ~300ms)
    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            index = (index + 1) % spinnerGlyphs.size
        }
    }

    FrostText(
        text = spinnerGlyphs[index],
        style = AppTheme.frostType.monoEmph,
        modifier =
            modifier
                .size(size)
                .semantics { liveRegion = LiveRegionMode.Polite },
    )
}

@Preview
@Composable
private fun FrostSpinnerPreview() {
    RatatoskrTheme {
        FrostSpinner()
    }
}
