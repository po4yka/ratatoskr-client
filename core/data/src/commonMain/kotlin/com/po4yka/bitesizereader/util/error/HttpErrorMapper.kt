package com.po4yka.bitesizereader.util.error

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

private val logger = KotlinLogging.logger {}

/**
 * Map HTTP status codes to AppError
 */
fun HttpStatusCode.toAppError(response: HttpResponse? = null): AppError {
    return when (value) {
        400 ->
            AppError.ValidationError(
                messageKey = "error.http.400",
                fallbackMessage = "Bad request. Please check your input.",
            )
        401 ->
            AppError.AuthError(
                messageKey = "error.auth.unauthorized",
                fallbackMessage = "Unauthorized. Please login again.",
            )
        403 ->
            AppError.AuthError(
                messageKey = "error.auth.forbidden",
                fallbackMessage = "Access forbidden. You don't have permission for this action.",
            )
        404 ->
            AppError.ServerError(
                code = 404,
                messageKey = "error.http.404",
                fallbackMessage = "Resource not found.",
            )
        408 ->
            AppError.TimeoutError(
                messageKey = "error.network.timeout",
                fallbackMessage = "Request timed out. Please try again.",
            )
        429 ->
            AppError.ServerError(
                code = 429,
                messageKey = "error.http.429",
                fallbackMessage = "Too many requests. Please try again later.",
            )
        500 ->
            AppError.ServerError(
                code = 500,
                messageKey = "error.http.500",
                fallbackMessage = "Internal server error.",
            )
        502 ->
            AppError.ServerError(
                code = 502,
                messageKey = "error.http.502",
                fallbackMessage = "Bad gateway. The server is having trouble processing your request.",
            )
        503 ->
            AppError.ServerError(
                code = 503,
                messageKey = "error.http.503",
                fallbackMessage = "Service unavailable. The server is temporarily down for maintenance.",
            )
        504 ->
            AppError.ServerError(
                code = 504,
                messageKey = "error.http.504",
                fallbackMessage = "Gateway timeout. The server took too long to respond.",
            )

        else -> {
            when {
                value in 400..499 ->
                    AppError.ValidationError(
                        messageKey = "error.http.$value",
                        fallbackMessage = "Request failed with code $value.",
                    )
                value in 500..599 ->
                    AppError.ServerError(
                        code = value,
                        messageKey = "error.http.$value",
                        fallbackMessage = "Server error $value.",
                    )
                else ->
                    AppError.UnknownError(
                        fallbackMessage = "Unexpected response code: $value",
                    )
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
        logger.error(e) { "API call failed with generic exception" }
        Result.failure(e.toAppError())
    }
}
