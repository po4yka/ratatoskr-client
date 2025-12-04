package com.po4yka.bitesizereader.util.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AppError : Throwable() {
    @Serializable
    @SerialName("network")
    data class NetworkError(
        override val message: String = "Network error. Please check your connection."
    ) : AppError()

    @Serializable
    @SerialName("server")
    data class ServerError(
        val code: Int,
        override val message: String
    ) : AppError()

    @Serializable
    @SerialName("auth")
    data class AuthError(
        override val message: String = "Authentication failed."
    ) : AppError()

    @Serializable
    @SerialName("validation")
    data class ValidationError(
        override val message: String
    ) : AppError()

    @Serializable
    @SerialName("unknown")
    data class UnknownError(
        override val message: String = "An unknown error occurred."
    ) : AppError()
}