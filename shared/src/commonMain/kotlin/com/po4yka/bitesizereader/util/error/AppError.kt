package com.po4yka.bitesizereader.util.error

/**
 * Sealed class representing application errors
 */
sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null
) {
    /**
     * Network related errors
     */
    data class NetworkError(
        override val message: String = "Network error. Please check your connection.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    /**
     * Server/API errors
     */
    data class ServerError(
        override val message: String = "Server error. Please try again later.",
        val statusCode: Int? = null,
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    /**
     * Authentication/Authorization errors
     */
    data class UnauthorizedError(
        override val message: String = "Session expired. Please login again.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    /**
     * Resource not found errors
     */
    data class NotFoundError(
        override val message: String = "Resource not found.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    /**
     * Validation errors
     */
    data class ValidationError(
        override val message: String,
        val field: String? = null,
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    /**
     * Database errors
     */
    data class DatabaseError(
        override val message: String = "Database error occurred.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    /**
     * Unknown/Generic errors
     */
    data class UnknownError(
        override val message: String = "An unexpected error occurred.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
}

/**
 * Convert exceptions to AppError
 */
fun Throwable.toAppError(): AppError {
    return when {
        // You can add specific exception type handling here
        message?.contains("network", ignoreCase = true) == true -> AppError.NetworkError(cause = this)
        message?.contains("401", ignoreCase = true) == true -> AppError.UnauthorizedError(cause = this)
        message?.contains("404", ignoreCase = true) == true -> AppError.NotFoundError(cause = this)
        message?.contains("5", ignoreCase = true) == true -> AppError.ServerError(cause = this)
        else -> AppError.UnknownError(message = message ?: "Unknown error", cause = this)
    }
}

/**
 * Get user-friendly error message
 */
fun AppError.getUserMessage(): String = message

/**
 * Check if error is retryable
 */
fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.NetworkError -> true
    is AppError.ServerError -> statusCode in 500..599
    else -> false
}
