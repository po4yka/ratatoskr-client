package com.po4yka.bitesizereader.util.retry

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Determines if an exception is retryable for network operations.
 * Retries on server errors (5xx) and network failures.
 * Does NOT retry on client errors (4xx) like auth failures.
 */
fun isRetryableException(e: Throwable): Boolean =
    when {
        e is CancellationException -> false
        e is ServerResponseException -> true
        e is ClientRequestException -> {
            val status = e.response.status
            status == HttpStatusCode.TooManyRequests || status == HttpStatusCode.RequestTimeout
        }
        e.cause != null -> isRetryableException(e.cause!!)
        else -> true
    }

data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelay: Duration = 1.seconds,
    val maxDelay: Duration = 10.seconds,
    val factor: Double = 2.0,
    val shouldRetry: (Throwable) -> Boolean = ::isRetryableException,
) {
    companion object {
        val DEFAULT = RetryPolicy()

        val AGGRESSIVE =
            RetryPolicy(
                maxAttempts = 5,
                initialDelay = 500.milliseconds,
                maxDelay = 30.seconds,
                factor = 2.5,
            )

        val CONSERVATIVE =
            RetryPolicy(
                maxAttempts = 2,
                initialDelay = 2.seconds,
                maxDelay = 5.seconds,
                factor = 1.5,
            )
    }
}

suspend fun <T> retryWithBackoff(
    policy: RetryPolicy = RetryPolicy.DEFAULT,
    block: suspend () -> T,
): T {
    var currentDelay = policy.initialDelay
    var lastException: Throwable? = null

    repeat(policy.maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            lastException = e
            if (!policy.shouldRetry(e) || attempt == policy.maxAttempts - 1) {
                throw e
            }
            delay(currentDelay)
            currentDelay = (currentDelay * policy.factor).coerceAtMost(policy.maxDelay)
        }
    }

    throw lastException ?: RuntimeException("Retry failed")
}

suspend fun <T> retry(
    times: Int = 3,
    initialDelayMillis: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    val policy =
        RetryPolicy(
            maxAttempts = times,
            initialDelay = initialDelayMillis.milliseconds,
            factor = factor,
        )
    return retryWithBackoff(policy, block)
}
