package com.po4yka.bitesizereader.ui.animation

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

    // Using default specs for simplicity across platforms.

    /**
     * Fade in animation
     */
    val fadeIn = fadeIn()

    /**
     * Fade out animation
     */
    val fadeOut = fadeOut()

    /**
     * Slide in from bottom animation
     */
    val slideInFromBottom =
        slideInVertically(
            initialOffsetY = { it },
        )

    /**
     * Slide out to bottom animation
     */
    val slideOutToBottom =
        slideOutVertically(
            targetOffsetY = { it },
        )
}
