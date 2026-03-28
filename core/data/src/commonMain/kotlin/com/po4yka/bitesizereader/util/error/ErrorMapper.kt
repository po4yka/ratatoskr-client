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
        ApiErrorCode.isRateLimitError(code) ->
            AppError.RateLimitError(
                retryAfterSeconds = retryAfter,
                fallbackMessage = message,
            )

        ApiErrorCode.isAuthError(code) ->
            when (errorCode) {
                ApiErrorCode.AUTH_TOKEN_EXPIRED,
                ApiErrorCode.AUTH_SESSION_EXPIRED,
                -> AppError.SessionExpiredError(fallbackMessage = message)
                else -> AppError.AuthError(fallbackMessage = message)
            }

        ApiErrorCode.isSyncError(code) ->
            AppError.SyncError(
                errorCode = code,
                fallbackMessage = message,
            )

        errorCode == ApiErrorCode.RESOURCE_NOT_FOUND -> AppError.NotFoundError(fallbackMessage = message)
        errorCode == ApiErrorCode.RESOURCE_ALREADY_EXISTS ||
            errorCode == ApiErrorCode.RESOURCE_VERSION_CONFLICT ->
            AppError.ConflictError(fallbackMessage = message)
        errorCode == ApiErrorCode.VALIDATION_FAILED ||
            errorCode == ApiErrorCode.VALIDATION_FIELD_REQUIRED ||
            errorCode == ApiErrorCode.VALIDATION_FIELD_INVALID ||
            errorCode == ApiErrorCode.VALIDATION_URL_INVALID ->
            AppError.ValidationError(fallbackMessage = message)
        errorCode == ApiErrorCode.AUTHZ_USER_NOT_ALLOWED ||
            errorCode == ApiErrorCode.AUTHZ_OWNER_REQUIRED ||
            errorCode == ApiErrorCode.AUTHZ_ACCESS_DENIED ->
            AppError.AuthError(
                messageKey = "error.auth.forbidden",
                fallbackMessage = message,
            )

        errorCode in
            listOf(
                ApiErrorCode.INTERNAL_ERROR,
                ApiErrorCode.INTERNAL_DATABASE_ERROR,
                ApiErrorCode.INTERNAL_CONFIG_ERROR,
                ApiErrorCode.EXTERNAL_FIRECRAWL_ERROR,
                ApiErrorCode.EXTERNAL_OPENROUTER_ERROR,
                ApiErrorCode.EXTERNAL_TELEGRAM_ERROR,
                ApiErrorCode.EXTERNAL_SERVICE_TIMEOUT,
                ApiErrorCode.EXTERNAL_SERVICE_UNAVAILABLE,
            )
        ->
            AppError.ServerError(
                code = 500,
                fallbackMessage = message,
            )

        else -> AppError.UnknownError(fallbackMessage = message)
    }
}
