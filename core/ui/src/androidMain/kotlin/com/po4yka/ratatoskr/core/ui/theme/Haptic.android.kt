package com.po4yka.ratatoskr.core.ui.theme

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberHaptic(): (HapticKind) -> Unit {
    val view = LocalView.current
    val enabled = LocalHapticEnabled.current
    return remember(view, enabled) {
        { kind ->
            if (enabled) {
                view.performHapticFeedback(kind.toAndroidConstant())
            }
        }
    }
}

private fun HapticKind.toAndroidConstant(): Int =
    when (this) {
        HapticKind.Confirm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }
        HapticKind.Reject ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                HapticFeedbackConstants.REJECT
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }
        HapticKind.Selection ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                HapticFeedbackConstants.CLOCK_TICK
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }
    }
