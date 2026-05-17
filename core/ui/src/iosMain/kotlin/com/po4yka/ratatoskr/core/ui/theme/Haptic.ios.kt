package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

@Composable
actual fun rememberHaptic(): (HapticKind) -> Unit {
    val enabled = LocalHapticEnabled.current
    val impact =
        remember {
            UIImpactFeedbackGenerator(
                style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight,
            )
        }
    val notification = remember { UINotificationFeedbackGenerator() }
    val selection = remember { UISelectionFeedbackGenerator() }
    return remember(enabled) {
        { kind ->
            if (enabled) {
                when (kind) {
                    HapticKind.Confirm -> impact.impactOccurred()
                    HapticKind.Reject ->
                        notification.notificationOccurred(
                            UINotificationFeedbackType.UINotificationFeedbackTypeError,
                        )
                    HapticKind.Selection -> selection.selectionChanged()
                }
            }
        }
    }
}
