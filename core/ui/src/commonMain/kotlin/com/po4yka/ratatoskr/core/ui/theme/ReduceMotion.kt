package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable

/**
 * Returns whether the user has requested reduced motion from the OS:
 *
 * - Android: `Settings.Global.TRANSITION_ANIMATION_SCALE == 0f` (covers
 *   the Developer Options "Transition animation scale" toggle and the
 *   accessibility "Remove animations" path).
 * - iOS: `UIAccessibility.isReduceMotionEnabled`.
 * - Desktop: always `false` — desktop is a development target.
 *
 * The implementation observes the underlying signal so recomposition
 * picks up runtime toggling without an app restart.
 *
 * Consumers should not call this directly when an animation runs inside
 * a Frost atom — those atoms already pick the right spec from
 * `AppTheme.motion`, which `RatatoskrTheme` swaps based on this value.
 */
@Composable
expect fun rememberReduceMotion(): Boolean
