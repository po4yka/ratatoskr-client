package com.po4yka.bitesizereader.util.error

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlin.math.pow

private val logger = KotlinLogging.logger {}

/**
 * Retry a suspending operation with the specified strategy
 */
suspend fun <T> retryWithStrategy(
    strategy: RetryStrategy,
    operation: suspend (attempt: Int) -> T
): Result<T> {
    when (strategy) {
        is RetryStrategy.NoRetry -> {
            return try {
                Result.success(operation(0))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        is RetryStrategy.Immediate -> {
            repeat(strategy.maxAttempts) { attempt ->
                try {
                    return Result.success(operation(attempt))
                } catch (e: Exception) {
                    if (attempt == strategy.maxAttempts - 1) {
                        logger.warn(e) { "All ${strategy.maxAttempts} immediate retry attempts failed" }
                        return Result.failure(e)
                    }
                    logger.debug { "Immediate retry attempt ${attempt + 1}/${strategy.maxAttempts} failed, retrying..." }
                    // No delay for immediate retry
                }
            }
        }

        is RetryStrategy.ExponentialBackoff -> {
            repeat(strategy.maxAttempts) { attempt ->
                try {
                    return Result.success(operation(attempt))
                } catch (e: Exception) {
                    if (attempt == strategy.maxAttempts - 1) {
                        logger.warn(e) { "All ${strategy.maxAttempts} exponential backoff attempts failed" }
                        return Result.failure(e)
                    }

                    val delayMs = strategy.getDelayForAttempt(attempt)
                    logger.debug { "Exponential backoff attempt ${attempt + 1}/${strategy.maxAttempts} failed, waiting ${delayMs}ms before retry..." }
                    delay(delayMs)
                }
            }
        }

        is RetryStrategy.FixedDelay -> {
            repeat(strategy.maxAttempts) { attempt ->
                try {
                    return Result.success(operation(attempt))
                } catch (e: Exception) {
                    if (attempt == strategy.maxAttempts - 1) {
                        logger.warn(e) { "All ${strategy.maxAttempts} fixed delay attempts failed" }
                        return Result.failure(e)
                    }

                    logger.debug { "Fixed delay attempt ${attempt + 1}/${strategy.maxAttempts} failed, waiting ${strategy.delayMs}ms before retry..." }
                    delay(strategy.delayMs)
                }
            }
        }

        is RetryStrategy.WhenOnline -> {
            // This strategy requires network monitoring
            // For now, treat as NoRetry and let higher layers handle queue
            return try {
                Result.success(operation(0))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    return Result.failure(IllegalStateException("Retry logic error"))
}

/**
 * Extension for easier retryable operations
 */
suspend fun <T> AppError.retryOperation(operation: suspend (attempt: Int) -> T): Result<T> {
    return retryWithStrategy(getRetryStrategy(), operation)
}

/**
 * Calculate exponential backoff delay
 */
fun calculateExponentialBackoff(
    attempt: Int,
    initialDelayMs: Long = 1000,
    maxDelayMs: Long = 32000,
    multiplier: Double = 2.0
): Long {
    val delay = initialDelayMs * multiplier.pow(attempt.toDouble()).toLong()
    return minOf(delay, maxDelayMs)
}
