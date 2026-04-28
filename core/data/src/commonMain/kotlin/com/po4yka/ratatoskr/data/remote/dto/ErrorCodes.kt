package com.po4yka.ratatoskr.data.remote.dto

/**
 * Machine-readable error codes matching the backend ErrorCode enum exactly.
 * Used for programmatic error handling in the client.
 */
enum class ApiErrorCode(val code: String) {
    // Authentication
    AUTH_TOKEN_EXPIRED("AUTH_TOKEN_EXPIRED"),
    AUTH_TOKEN_INVALID("AUTH_TOKEN_INVALID"),
    AUTH_SESSION_EXPIRED("AUTH_SESSION_EXPIRED"),
    AUTH_CREDENTIALS_INVALID("AUTH_CREDENTIALS_INVALID"),
    AUTH_SECRET_LOCKED("AUTH_SECRET_LOCKED"),
    AUTH_SECRET_REVOKED("AUTH_SECRET_REVOKED"),

    // Authorization
    AUTHZ_USER_NOT_ALLOWED("AUTHZ_USER_NOT_ALLOWED"),
    AUTHZ_CLIENT_NOT_ALLOWED("AUTHZ_CLIENT_NOT_ALLOWED"),
    AUTHZ_OWNER_REQUIRED("AUTHZ_OWNER_REQUIRED"),
    AUTHZ_ACCESS_DENIED("AUTHZ_ACCESS_DENIED"),

    // Validation
    VALIDATION_FAILED("VALIDATION_FAILED"),
    VALIDATION_FIELD_REQUIRED("VALIDATION_FIELD_REQUIRED"),
    VALIDATION_FIELD_INVALID("VALIDATION_FIELD_INVALID"),
    VALIDATION_URL_INVALID("VALIDATION_URL_INVALID"),

    // Resource
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    RESOURCE_ALREADY_EXISTS("RESOURCE_ALREADY_EXISTS"),
    RESOURCE_VERSION_CONFLICT("RESOURCE_VERSION_CONFLICT"),

    // Rate limit
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED"),

    // External services
    EXTERNAL_FIRECRAWL_ERROR("EXTERNAL_FIRECRAWL_ERROR"),
    EXTERNAL_OPENROUTER_ERROR("EXTERNAL_OPENROUTER_ERROR"),
    EXTERNAL_TELEGRAM_ERROR("EXTERNAL_TELEGRAM_ERROR"),
    EXTERNAL_SERVICE_TIMEOUT("EXTERNAL_SERVICE_TIMEOUT"),
    EXTERNAL_SERVICE_UNAVAILABLE("EXTERNAL_SERVICE_UNAVAILABLE"),

    // Internal
    INTERNAL_ERROR("INTERNAL_ERROR"),
    INTERNAL_DATABASE_ERROR("INTERNAL_DATABASE_ERROR"),
    INTERNAL_CONFIG_ERROR("INTERNAL_CONFIG_ERROR"),

    // Sync
    SYNC_SESSION_EXPIRED("SYNC_SESSION_EXPIRED"),
    SYNC_SESSION_NOT_FOUND("SYNC_SESSION_NOT_FOUND"),
    SYNC_SESSION_FORBIDDEN("SYNC_SESSION_FORBIDDEN"),
    SYNC_NO_CHANGES("SYNC_NO_CHANGES"),
    SYNC_CONFLICT("SYNC_CONFLICT"),
    SYNC_INVALID_ENTITY("SYNC_INVALID_ENTITY"),
    SYNC_ENTITY_NOT_FOUND("SYNC_ENTITY_NOT_FOUND"),
    ;

    companion object {
        fun fromCode(code: String): ApiErrorCode? = entries.find { it.code == code }

        fun isAuthError(code: String): Boolean {
            val errorCode = fromCode(code)
            return errorCode in
                listOf(
                    AUTH_TOKEN_EXPIRED,
                    AUTH_TOKEN_INVALID,
                    AUTH_SESSION_EXPIRED,
                    AUTH_CREDENTIALS_INVALID,
                    AUTH_SECRET_REVOKED,
                    AUTH_SECRET_LOCKED,
                )
        }

        fun isRateLimitError(code: String): Boolean {
            val errorCode = fromCode(code)
            return errorCode == RATE_LIMIT_EXCEEDED
        }

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
 * Error type categories matching the backend ErrorType enum.
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
    ;

    companion object {
        fun fromType(type: String): ApiErrorType? = entries.find { it.type == type }
    }
}
