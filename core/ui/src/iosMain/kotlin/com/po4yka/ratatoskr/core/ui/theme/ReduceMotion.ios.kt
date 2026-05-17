package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityReduceMotionStatusDidChangeNotification

@Composable
actual fun rememberReduceMotion(): Boolean {
    var reduce by remember { mutableStateOf(UIAccessibilityIsReduceMotionEnabled()) }
    DisposableEffect(Unit) {
        val center = NSNotificationCenter.defaultCenter
        val handle =
            center.addObserverForName(
                name = UIAccessibilityReduceMotionStatusDidChangeNotification,
                `object` = null,
                queue = null,
            ) { _ -> reduce = UIAccessibilityIsReduceMotionEnabled() }
        onDispose { center.removeObserver(handle) }
    }
    return reduce
}
