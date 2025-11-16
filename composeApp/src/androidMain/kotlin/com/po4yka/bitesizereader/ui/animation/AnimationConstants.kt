package com.po4yka.bitesizereader.ui.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

/**
 * Animation constants and presets for the app
 */
object AnimationConstants {

    /**
     * Standard duration for most animations (ms)
     */
    const val STANDARD_DURATION = 300

    /**
     * Fast duration for quick transitions (ms)
     */
    const val FAST_DURATION = 150

    /**
     * Slow duration for emphasized transitions (ms)
     */
    const val SLOW_DURATION = 500

    /**
     * Standard easing for material design
     */
    val STANDARD_EASING = tween<Float>(STANDARD_DURATION)

    /**
     * Fast easing for quick transitions
     */
    val FAST_EASING = tween<Float>(FAST_DURATION)

    /**
     * Spring animation for bouncy effects
     */
    val SPRING_ANIMATION = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Fade in animation
     */
    val fadeIn = fadeIn(animationSpec = STANDARD_EASING)

    /**
     * Fade out animation
     */
    val fadeOut = fadeOut(animationSpec = STANDARD_EASING)

    /**
     * Slide in from bottom animation
     */
    val slideInFromBottom = slideInVertically(
        initialOffsetY = { it },
        animationSpec = STANDARD_EASING
    )

    /**
     * Slide out to bottom animation
     */
    val slideOutToBottom = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = STANDARD_EASING
    )
}
