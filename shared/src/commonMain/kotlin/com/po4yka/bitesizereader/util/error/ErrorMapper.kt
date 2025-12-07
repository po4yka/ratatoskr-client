package com.po4yka.bitesizereader.util.error

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException

fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is RedirectResponseException -> AppError.ServerError(response.status.value, message)
        is ClientRequestException -> {
            if (response.status.value == 401) {
                AppError.AuthError()
            } else {
                AppError.ServerError(response.status.value, message)
            }
        }
        is ServerResponseException -> AppError.ServerError(response.status.value, message)
        is IOException -> AppError.NetworkError()
        else -> AppError.UnknownError(message ?: "Unknown error")
    }
}
