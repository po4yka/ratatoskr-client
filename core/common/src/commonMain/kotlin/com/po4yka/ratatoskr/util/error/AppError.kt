package com.po4yka.ratatoskr.util.error

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

    @Serializable
    @SerialName("storage")
    data class StorageError(
        override val messageKey: String = "error.storage.nospace",
        override val fallbackMessage: String = "Not enough space on device. Please free up some space and try again.",
    ) : AppError()

    @Serializable
    @SerialName("rate_limit")
    data class RateLimitError(
        val retryAfterSeconds: Int? = null,
        override val messageKey: String = "error.rate_limit",
        override val fallbackMessage: String = "Too many requests. Please try again later.",
    ) : AppError()

    @Serializable
    @SerialName("sync")
    data class SyncError(
        val errorCode: String? = null,
        override val messageKey: String = "error.sync",
        override val fallbackMessage: String = "Sync failed. Please try again.",
    ) : AppError()

    @Serializable
    @SerialName("not_found")
    data class NotFoundError(
        override val messageKey: String = "error.not_found",
        override val fallbackMessage: String = "The requested resource was not found.",
    ) : AppError()

    @Serializable
    @SerialName("conflict")
    data class ConflictError(
        override val messageKey: String = "error.conflict",
        override val fallbackMessage: String = "A conflict occurred. Please refresh and try again.",
    ) : AppError()
}
