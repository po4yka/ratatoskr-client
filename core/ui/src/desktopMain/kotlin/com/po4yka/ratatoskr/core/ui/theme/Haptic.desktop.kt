package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable

/**
 * Desktop has no first-class haptic affordance — return a no-op emitter so
 * Frost atoms keep the same call shape on every target.
 */
@Composable
actual fun rememberHaptic(): (HapticKind) -> Unit = { _ -> }
