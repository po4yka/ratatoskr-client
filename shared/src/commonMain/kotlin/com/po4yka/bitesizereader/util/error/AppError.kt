package com.po4yka.bitesizereader.util.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AppError : Throwable() {
    abstract val messageKey: String
    abstract val fallbackMessage: String?

    override val message: String?
        get() = fallbackMessage

    @Serializable
    @SerialName("network")
    data class NetworkError(
        override val messageKey: String = "error.network.unreachable",
        override val fallbackMessage: String = "Network connection unavailable. Please check your connection.",
    ) : AppError()

    @Serializable
    @SerialName("timeout")
    data class TimeoutError(
        override val messageKey: String = "error.network.timeout",
        override val fallbackMessage: String = "Request timed out. Please try again.",
    ) : AppError()

    @Serializable
    @SerialName("server")
    data class ServerError(
        val code: Int,
        override val messageKey: String = "error.http.$code",
        override val fallbackMessage: String = "Request failed with code $code.",
    ) : AppError()

    @Serializable
    @SerialName("auth")
    data class AuthError(
        override val messageKey: String = "error.auth.unauthorized",
        override val fallbackMessage: String = "Authentication failed.",
    ) : AppError()

    @Serializable
    @SerialName("session_expired")
    data class SessionExpiredError(
        override val messageKey: String = "error.auth.session_expired",
        override val fallbackMessage: String = "Your session has expired. Please log in again.",
    ) : AppError()

    @Serializable
    @SerialName("validation")
    data class ValidationError(
        override val messageKey: String = "error.validation",
        override val fallbackMessage: String,
    ) : AppError()

    @Serializable
    @SerialName("unknown")
    data class UnknownError(
        override val messageKey: String = "error.unknown",
        override val fallbackMessage: String = "Something went wrong. Please try again.",
    ) : AppError()
}
