package com.po4yka.bitesizereader.data.remote.dto

/**
 * Machine-readable error codes matching OpenAPI ErrorObject.code enum.
 * Used for programmatic error handling in the client.
 */
enum class ApiErrorCode(val code: String) {
    // Client errors
    VALIDATION_ERROR("VALIDATION_ERROR"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    NOT_FOUND("NOT_FOUND"),
    CONFLICT("CONFLICT"),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED"),
    SESSION_EXPIRED("SESSION_EXPIRED"),

    // Token/Auth errors
    TOKEN_EXPIRED("TOKEN_EXPIRED"),
    TOKEN_INVALID("TOKEN_INVALID"),
    TOKEN_REVOKED("TOKEN_REVOKED"),
    TOKEN_WRONG_TYPE("TOKEN_WRONG_TYPE"),
    REFRESH_RATE_LIMITED("REFRESH_RATE_LIMITED"),

    // Sync errors
    SYNC_SESSION_EXPIRED("SYNC_SESSION_EXPIRED"),
    SYNC_SESSION_NOT_FOUND("SYNC_SESSION_NOT_FOUND"),
    SYNC_SESSION_FORBIDDEN("SYNC_SESSION_FORBIDDEN"),
    SYNC_NO_CHANGES("SYNC_NO_CHANGES"),
    SYNC_CONFLICT("SYNC_CONFLICT"),
    SYNC_INVALID_ENTITY("SYNC_INVALID_ENTITY"),
    SYNC_ENTITY_NOT_FOUND("SYNC_ENTITY_NOT_FOUND"),

    // Server errors
    INTERNAL_ERROR("INTERNAL_ERROR"),
    DATABASE_ERROR("DATABASE_ERROR"),
    EXTERNAL_API_ERROR("EXTERNAL_API_ERROR"),
    PROCESSING_ERROR("PROCESSING_ERROR"),
    AUTH_SERVICE_UNAVAILABLE("AUTH_SERVICE_UNAVAILABLE"),

    // Configuration errors
    CONFIGURATION_ERROR("CONFIGURATION_ERROR"),
    FEATURE_DISABLED("FEATURE_DISABLED"),
    ;

    companion object {
        /**
         * Parse error code string to enum value.
         * Returns null if code is not recognized.
         */
        fun fromCode(code: String): ApiErrorCode? = entries.find { it.code == code }

        /**
         * Check if the error code indicates an authentication issue.
         */
        fun isAuthError(code: String): Boolean {
            val errorCode = fromCode(code)
            return errorCode in
                listOf(
                    UNAUTHORIZED,
                    TOKEN_EXPIRED,
                    TOKEN_INVALID,
                    TOKEN_REVOKED,
                    TOKEN_WRONG_TYPE,
                )
        }

        /**
         * Check if the error code indicates a rate limit issue.
         */
        fun isRateLimitError(code: String): Boolean {
            val errorCode = fromCode(code)
            return errorCode in listOf(RATE_LIMIT_EXCEEDED, REFRESH_RATE_LIMITED)
        }

        /**
         * Check if the error code indicates a sync-related issue.
         */
        fun isSyncError(code: String): Boolean {
            val errorCode = fromCode(code)
            return errorCode in
                listOf(
                    SYNC_SESSION_EXPIRED,
                    SYNC_SESSION_NOT_FOUND,
                    SYNC_SESSION_FORBIDDEN,
                    SYNC_NO_CHANGES,
                    SYNC_CONFLICT,
                    SYNC_INVALID_ENTITY,
                    SYNC_ENTITY_NOT_FOUND,
                )
        }
    }
}

/**
 * Error type categories matching OpenAPI ErrorObject.errorType enum.
 * Used for client-side handling logic decisions.
 */
enum class ApiErrorType(val type: String) {
    AUTHENTICATION("authentication"),
    AUTHORIZATION("authorization"),
    VALIDATION("validation"),
    NOT_FOUND("not_found"),
    CONFLICT("conflict"),
    RATE_LIMIT("rate_limit"),
    EXTERNAL_SERVICE("external_service"),
    SYNC("sync"),
    INTERNAL("internal"),
    CONFIGURATION("configuration"),
    ;

    companion object {
        fun fromType(type: String): ApiErrorType? = entries.find { it.type == type }
    }
}
