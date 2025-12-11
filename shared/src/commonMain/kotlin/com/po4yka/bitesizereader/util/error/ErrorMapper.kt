package com.po4yka.bitesizereader.util.error

import com.po4yka.bitesizereader.data.remote.dto.ApiErrorCode
import com.po4yka.bitesizereader.data.remote.dto.ErrorResponseDto
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is CancellationException -> throw this
        is RedirectResponseException -> response.status.toAppError(response)
        is ClientRequestException -> response.status.toAppError(response)
        is ServerResponseException -> response.status.toAppError(response)
        is HttpRequestTimeoutException -> AppError.TimeoutError()
        is IOException -> AppError.NetworkError()
        else -> AppError.UnknownError(fallbackMessage = message ?: "Unknown error")
    }
}

/**
 * Map API ErrorResponseDto to AppError using structured error codes.
 * This provides more precise error handling than HTTP status codes alone.
 */
fun ErrorResponseDto.toAppError(): AppError {
    val errorCode = ApiErrorCode.fromCode(code)

    return when {
        // Rate limit errors
        ApiErrorCode.isRateLimitError(code) ->
            AppError.RateLimitError(
                retryAfterSeconds = retryAfter,
                fallbackMessage = message,
            )

        // Auth errors
        ApiErrorCode.isAuthError(code) ->
            when (errorCode) {
                ApiErrorCode.TOKEN_EXPIRED,
                ApiErrorCode.SESSION_EXPIRED,
                -> AppError.SessionExpiredError(fallbackMessage = message)
                else -> AppError.AuthError(fallbackMessage = message)
            }

        // Sync errors
        ApiErrorCode.isSyncError(code) ->
            AppError.SyncError(
                errorCode = code,
                fallbackMessage = message,
            )

        // Specific error codes
        errorCode == ApiErrorCode.NOT_FOUND -> AppError.NotFoundError(fallbackMessage = message)
        errorCode == ApiErrorCode.CONFLICT -> AppError.ConflictError(fallbackMessage = message)
        errorCode == ApiErrorCode.VALIDATION_ERROR -> AppError.ValidationError(fallbackMessage = message)
        errorCode == ApiErrorCode.FORBIDDEN ->
            AppError.AuthError(
                messageKey = "error.auth.forbidden",
                fallbackMessage = message,
            )

        // Server errors
        errorCode in
            listOf(
                ApiErrorCode.INTERNAL_ERROR,
                ApiErrorCode.DATABASE_ERROR,
                ApiErrorCode.EXTERNAL_API_ERROR,
                ApiErrorCode.PROCESSING_ERROR,
            )
        ->
            AppError.ServerError(
                code = 500,
                fallbackMessage = message,
            )

        // Default
        else -> AppError.UnknownError(fallbackMessage = message)
    }
}
