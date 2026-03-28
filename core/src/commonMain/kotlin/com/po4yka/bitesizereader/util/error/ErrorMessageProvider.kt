package com.po4yka.bitesizereader.util.error

interface ErrorMessageProvider {
    fun messageFor(
        key: String,
        fallback: String? = null,
    ): String
}

object DefaultErrorMessageProvider : ErrorMessageProvider {
    private val messages =
        mapOf(
            "error.network.unreachable" to "Network connection unavailable. Please check your connection.",
            "error.network.timeout" to "Request timed out. Please try again.",
            "error.auth.unauthorized" to "Unauthorized. Please log in again.",
            "error.auth.forbidden" to "You don't have permission to perform this action.",
            "error.http.400" to "Bad request. Please check your input.",
            "error.http.404" to "Resource not found.",
            "error.http.429" to "Too many requests. Please try again later.",
            "error.http.500" to "Internal server error.",
            "error.http.502" to "Bad gateway. The server had trouble processing your request.",
            "error.http.503" to "Service unavailable. Please try again shortly.",
            "error.http.504" to "Gateway timeout. The server took too long to respond.",
            "error.unknown" to "Something went wrong. Please try again.",
        )

    override fun messageFor(
        key: String,
        fallback: String?,
    ): String {
        return messages[key] ?: fallback ?: "Something went wrong. Please try again."
    }
}

fun AppError.userMessage(provider: ErrorMessageProvider = DefaultErrorMessageProvider): String {
    return provider.messageFor(messageKey, fallbackMessage)
}
