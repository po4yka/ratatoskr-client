package com.po4yka.bitesizereader.util.error

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
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
        else -> AppError.UnknownError(message ?: "Unknown error")
    }
}
