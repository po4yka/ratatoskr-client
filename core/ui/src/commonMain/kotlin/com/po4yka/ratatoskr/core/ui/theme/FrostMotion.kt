package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable

/**
 * Frost motion tokens.
 *
 * Use [frostMotionDefault] in normal conditions.
 * Use [frostMotionReduced] when the user has requested reduced motion (check
 * `LocalAccessibilityManager.current?.isTraversalGroup` or platform reduced-motion setting).
 */
@Immutable
data class FrostMotion(
    val blinker: InfiniteRepeatableSpec<Float>,
    val pulse: InfiniteRepeatableSpec<Float>,
    val toast: AnimationSpec<Float>,
    val clickPress: AnimationSpec<Float>,
    val selectPulse: AnimationSpec<Float>,
    val dragLift: AnimationSpec<Float>,
    val undoFade: AnimationSpec<Float>,
)

val frostMotionDefault =
    FrostMotion(
        blinker = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        pulse = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        toast = tween(120, easing = LinearEasing),
        clickPress = tween(80, easing = LinearEasing),
        selectPulse = tween(200, easing = EaseOut),
        dragLift = tween(150, easing = LinearEasing),
        undoFade = tween(400, easing = LinearEasing),
    )

val frostMotionReduced =
    FrostMotion(
        blinker = infiniteRepeatable(snap()),
        pulse = infiniteRepeatable(snap()),
        toast = snap(),
        clickPress = snap(),
        selectPulse = snap(),
        dragLift = snap(),
        undoFade = tween(1),
    )
