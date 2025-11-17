package com.po4yka.bitesizereader.util.error

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

/**
 * Map HTTP status codes to AppError
 */
fun HttpStatusCode.toAppError(response: HttpResponse? = null): AppError {
    return when (value) {
        // 4xx Client Errors
        400 -> AppError.ValidationError("Bad request. Please check your input.")
        401 -> AppError.UnauthorizedError()
        403 -> AppError.UnauthorizedError("Access forbidden. You don't have permission for this action.")
        404 -> AppError.NotFoundError()
        408 -> AppError.NetworkError(message = "Request timeout. Please try again.", isTimeout = true)
        429 -> AppError.RateLimitError()

        // 5xx Server Errors
        500 -> AppError.ServerError(statusCode = 500)
        502 -> AppError.ServerError(
            message = "Bad gateway. The server is having trouble processing your request.",
            statusCode = 502
        )
        503 -> AppError.ServerError(
            message = "Service unavailable. The server is temporarily down for maintenance.",
            statusCode = 503
        )
        504 -> AppError.ServerError(
            message = "Gateway timeout. The server took too long to respond.",
            statusCode = 504
        )

        else -> {
            if (value in 400..499) {
                AppError.ValidationError("Request failed with code $value")
            } else if (value in 500..599) {
                AppError.ServerError(statusCode = value)
            } else {
                AppError.UnknownError("Unexpected response code: $value")
            }
        }
    }
}

/**
 * Convert Ktor exceptions to AppError
 */
fun Exception.toKtorError(): AppError {
    return when (this) {
        is ClientRequestException -> response.status.toAppError(response)
        is ServerResponseException -> response.status.toAppError(response)
        else -> toAppError()
    }
}

/**
 * Handle API response errors
 */
suspend fun <T> handleApiError(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: ClientRequestException) {
        Result.failure(e.response.status.toAppError(e.response))
    } catch (e: ServerResponseException) {
        Result.failure(e.response.status.toAppError(e.response))
    } catch (e: Exception) {
        Result.failure(e.toAppError())
    }
}
