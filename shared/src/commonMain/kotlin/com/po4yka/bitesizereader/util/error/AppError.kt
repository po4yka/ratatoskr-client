package com.po4yka.bitesizereader.util.error

/**
 * Sealed class representing application errors with user-friendly messages
 * and retry strategies
 */
sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null,
) {
    /**
     * Network connectivity errors (no internet, timeout, etc.)
     */
    data class NetworkError(
        override val message: String = "No internet connection. Please check your network and try again.",
        override val cause: Throwable? = null,
        val isTimeout: Boolean = false,
    ) : AppError(message, cause)

    /**
     * Server/API errors (5xx status codes)
     */
    data class ServerError(
        override val message: String = "The server is temporarily unavailable. Please try again in a few moments.",
        val statusCode: Int? = null,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Authentication/Authorization errors (401, 403)
     */
    data class UnauthorizedError(
        override val message: String = "Your session has expired. Please log in again to continue.",
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Resource not found errors (404)
     */
    data class NotFoundError(
        override val message: String = "The requested content could not be found.",
        val resource: String? = null,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Client-side validation errors (400)
     */
    data class ValidationError(
        override val message: String,
        val field: String? = null,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Rate limiting errors (429)
     */
    data class RateLimitError(
        override val message: String = "You're making too many requests. Please slow down and try again later.",
        val retryAfterSeconds: Int? = null,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Database/storage errors
     */
    data class DatabaseError(
        override val message: String = "A storage error occurred. Your data may not have been saved.",
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Content parsing errors
     */
    data class ParsingError(
        override val message: String = "Unable to process the content. The URL may contain unsupported content.",
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Offline mode - operation requires network
     */
    data class OfflineError(
        override val message: String = "You're offline. This action will be completed when you reconnect.",
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /**
     * Unknown/Generic errors
     */
    data class UnknownError(
        override val message: String = "Something went wrong. Please try again.",
        override val cause: Throwable? = null,
    ) : AppError(message, cause)
}

/**
 * Convert exceptions to AppError with intelligent error detection
 */
fun Throwable.toAppError(): AppError {
    val errorMessage = message?.lowercase() ?: ""

    return when {
        // Network-related errors
        errorMessage.contains("network") ||
        errorMessage.contains("connection") ||
        errorMessage.contains("unreachable") ||
        errorMessage.contains("no internet") ->
            AppError.NetworkError(cause = this)

        // Timeout errors
        errorMessage.contains("timeout") ||
        errorMessage.contains("timed out") ->
            AppError.NetworkError(isTimeout = true, cause = this)

        // HTTP status code errors
        errorMessage.contains("401") || errorMessage.contains("unauthorized") ->
            AppError.UnauthorizedError(cause = this)

        errorMessage.contains("403") || errorMessage.contains("forbidden") ->
            AppError.UnauthorizedError(
                message = "Access forbidden. You don't have permission to access this resource.",
                cause = this
            )

        errorMessage.contains("404") || errorMessage.contains("not found") ->
            AppError.NotFoundError(cause = this)

        errorMessage.contains("429") || errorMessage.contains("rate limit") || errorMessage.contains("too many requests") ->
            AppError.RateLimitError(cause = this)

        errorMessage.contains("500") || errorMessage.contains("502") ||
        errorMessage.contains("503") || errorMessage.contains("504") ||
        errorMessage.contains("server error") ->
            AppError.ServerError(cause = this)

        // Database errors
        errorMessage.contains("database") || errorMessage.contains("sql") ->
            AppError.DatabaseError(cause = this)

        // Parsing errors
        errorMessage.contains("parse") || errorMessage.contains("json") ||
        errorMessage.contains("serialization") ->
            AppError.ParsingError(cause = this)

        // Validation errors
        errorMessage.contains("validation") || errorMessage.contains("invalid") ->
            AppError.ValidationError(message = message ?: "Validation failed", cause = this)

        else -> AppError.UnknownError(message = message ?: "An unexpected error occurred", cause = this)
    }
}

/**
 * Get user-friendly error message
 */
fun AppError.getUserMessage(): String = message

/**
 * Get short error title for snackbars/toasts
 */
fun AppError.getErrorTitle(): String = when (this) {
    is AppError.NetworkError -> if (isTimeout) "Connection Timeout" else "No Connection"
    is AppError.ServerError -> "Server Error"
    is AppError.UnauthorizedError -> "Authentication Required"
    is AppError.NotFoundError -> "Not Found"
    is AppError.ValidationError -> "Invalid Input"
    is AppError.RateLimitError -> "Too Many Requests"
    is AppError.DatabaseError -> "Storage Error"
    is AppError.ParsingError -> "Content Error"
    is AppError.OfflineError -> "Offline"
    is AppError.UnknownError -> "Error"
}

/**
 * Check if error is retryable
 */
fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.NetworkError -> true
    is AppError.ServerError -> statusCode == null || statusCode in 500..599
    is AppError.RateLimitError -> true
    is AppError.OfflineError -> true
    else -> false
}

/**
 * Get retry strategy for this error
 */
fun AppError.getRetryStrategy(): RetryStrategy = when (this) {
    is AppError.NetworkError ->
        if (isTimeout) RetryStrategy.ExponentialBackoff(maxAttempts = 3)
        else RetryStrategy.Immediate(maxAttempts = 5)

    is AppError.ServerError ->
        RetryStrategy.ExponentialBackoff(maxAttempts = 3, initialDelayMs = 2000)

    is AppError.RateLimitError ->
        RetryStrategy.FixedDelay(
            maxAttempts = 3,
            delayMs = (retryAfterSeconds ?: 60) * 1000L
        )

    is AppError.OfflineError ->
        RetryStrategy.WhenOnline

    else -> RetryStrategy.NoRetry
}

/**
 * Retry strategies for different error types
 */
sealed class RetryStrategy {
    /**
     * No retry - error is not retryable
     */
    data object NoRetry : RetryStrategy()

    /**
     * Retry immediately (for network blips)
     */
    data class Immediate(val maxAttempts: Int = 3) : RetryStrategy()

    /**
     * Retry with exponential backoff (for server errors)
     */
    data class ExponentialBackoff(
        val maxAttempts: Int = 3,
        val initialDelayMs: Long = 1000,
        val maxDelayMs: Long = 32000,
        val multiplier: Double = 2.0
    ) : RetryStrategy() {
        fun getDelayForAttempt(attempt: Int): Long {
            val delay = initialDelayMs * kotlin.math.pow(multiplier, attempt.toDouble()).toLong()
            return minOf(delay, maxDelayMs)
        }
    }

    /**
     * Retry with fixed delay (for rate limits)
     */
    data class FixedDelay(
        val maxAttempts: Int = 3,
        val delayMs: Long = 5000
    ) : RetryStrategy()

    /**
     * Retry when connection is restored
     */
    data object WhenOnline : RetryStrategy()
}

/**
 * Check if error requires user action (not auto-retryable)
 */
fun AppError.requiresUserAction(): Boolean = when (this) {
    is AppError.UnauthorizedError -> true // Need to re-login
    is AppError.ValidationError -> true // Need to fix input
    is AppError.NotFoundError -> true // Content doesn't exist
    else -> false
}
