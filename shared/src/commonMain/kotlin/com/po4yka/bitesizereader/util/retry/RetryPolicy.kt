package com.po4yka.bitesizereader.util.retry

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Retry policy configuration
 */
data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelay: Duration = 1.seconds,
    val maxDelay: Duration = 10.seconds,
    val factor: Double = 2.0,
    val shouldRetry: (Throwable) -> Boolean = { true }
) {
    companion object {
        /**
         * Default retry policy for network operations
         */
        val DEFAULT = RetryPolicy()

        /**
         * Aggressive retry policy with more attempts
         */
        val AGGRESSIVE = RetryPolicy(
            maxAttempts = 5,
            initialDelay = 500.milliseconds,
            maxDelay = 30.seconds,
            factor = 2.5
        )

        /**
         * Conservative retry policy with fewer attempts
         */
        val CONSERVATIVE = RetryPolicy(
            maxAttempts = 2,
            initialDelay = 2.seconds,
            maxDelay = 5.seconds,
            factor = 1.5
        )
    }
}

/**
 * Retry a suspend function with exponential backoff
 */
suspend fun <T> retryWithBackoff(
    policy: RetryPolicy = RetryPolicy.DEFAULT,
    block: suspend () -> T
): T {
    var currentDelay = policy.initialDelay
    var lastException: Throwable? = null

    repeat(policy.maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            lastException = e

            // Check if we should retry
            if (!policy.shouldRetry(e) || attempt == policy.maxAttempts - 1) {
                throw e
            }

            // Wait before retry with exponential backoff
            delay(currentDelay)

            // Calculate next delay
            currentDelay = (currentDelay * policy.factor)
                .coerceAtMost(policy.maxDelay)
        }
    }

    // Should never reach here, but throw the last exception if it does
    throw lastException ?: RuntimeException("Retry failed")
}

/**
 * Retry a block with a simple retry count
 */
suspend fun <T> retry(
    times: Int = 3,
    initialDelayMillis: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    val policy = RetryPolicy(
        maxAttempts = times,
        initialDelay = initialDelayMillis.milliseconds,
        factor = factor
    )
    return retryWithBackoff(policy, block)
}
