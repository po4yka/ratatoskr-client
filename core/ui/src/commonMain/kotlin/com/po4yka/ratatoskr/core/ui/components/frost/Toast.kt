@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme
import kotlinx.coroutines.delay

/**
 * Severity levels for [ToastState].
 */
enum class ToastSeverity { Info, Warn, Alarm }

/**
 * State holder for [ToastHost]. Call [show] to display a message.
 */
@Stable
class ToastState {
    var message: String? by mutableStateOf(null)
        private set
    var severity: ToastSeverity by mutableStateOf(ToastSeverity.Info)
        private set

    /**
     * Shows [message] at [severity] level and auto-dismisses after 3 seconds.
     */
    suspend fun show(
        message: String,
        severity: ToastSeverity = ToastSeverity.Info,
    ) {
        this.message = message
        this.severity = severity
        delay(3_000)
        this.message = null
    }
}

/**
 * Frost toast host: renders a [BrutalistCard] toast anchored at the bottom of its container.
 *
 * Alarm severity uses critical card variant (spark bar). Auto-dismissed after 3s via [ToastState.show].
 * Implements DESIGN.md § Components — Toast, Spark Accent Policy.
 */
@Composable
fun ToastHost(
    state: ToastState,
    modifier: Modifier = Modifier,
) {
    val message = state.message
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            if (message != null) {
                BrutalistCard(
                    critical = state.severity == ToastSeverity.Alarm,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.spacing.line),
                ) {
                    FrostText(
                        text = message,
                        style = AppTheme.frostType.monoBody,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ToastHostPreview() {
    RatatoskrTheme {
        val state = ToastState()
        LaunchedEffect(Unit) {
            state.show("Sync failed — check connection", ToastSeverity.Alarm)
        }
        ToastHost(state = state)
    }
}
