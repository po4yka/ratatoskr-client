package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Frost haptic kinds — the only three the design system fires.
 *
 * - [Confirm]: success path acknowledgment (primary button tap, switch on,
 *   swipe-archive). Maps to `CONFIRM` on Android 14+, light impact on iOS.
 * - [Reject]: failure/blocked path (validation error, undo-not-available).
 *   Maps to `REJECT` on Android 14+, light impact on iOS.
 * - [Selection]: small "you moved the selection" tick (slider step, chip
 *   toggle). Maps to `CLOCK_TICK` on Android, selection generator on iOS.
 */
enum class HapticKind { Confirm, Reject, Selection }

/**
 * Composable hook returning a haptic emitter. Frost atoms call this on the
 * success path of direct user actions (button taps, switches, swipes); they
 * do not fire haptics from background events or recomposition.
 *
 * The emitter honors [LocalHapticEnabled]: when the surrounding theme says
 * haptics are off the call is a no-op. The OS-level haptic-feedback setting
 * is honored automatically by each platform's underlying API (Android's
 * `View.performHapticFeedback` already respects `Settings.System.HAPTIC_FEEDBACK_ENABLED`).
 */
@Composable
expect fun rememberHaptic(): (HapticKind) -> Unit

/**
 * App-level kill switch for haptics — wire to a Settings preference when one
 * is added. Defaults to on; setting it `false` silences every Frost atom's
 * haptic without changing call sites.
 */
val LocalHapticEnabled = staticCompositionLocalOf { true }
