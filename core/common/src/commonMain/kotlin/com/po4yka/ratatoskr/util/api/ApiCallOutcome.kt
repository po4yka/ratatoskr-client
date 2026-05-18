package com.po4yka.ratatoskr.util.api

/**
 * Unified outcome for an HTTP API call that smooths over the difference
 * between the hand-written `ApiClient` (returning `ApiResponseDto<T>`)
 * and the generated openapi-kmp-gen `Api` (returning
 * `Either<CallException, HttpCallResponse<T>>`). Lets feature modules
 * pattern-match on one sealed hierarchy instead of two during the
 * complete-generated-client-migration window.
 *
 * Variants are chosen to match the existing branch points already in
 * the codebase:
 *  - [Success]         — 2xx response, optional body
 *  - [Unauthorized]    — 401, triggers SharedTokenRefresher
 *  - [NotFound]        — 404, surfaces as a domain "not found"
 *  - [Conflict]        — 409, drives sync conflict count
 *  - [RateLimited]     — 429, drives PendingOpRetryPolicy.RateLimited
 *  - [ClientError]     — other 4xx, includes status for diagnostics
 *  - [ServerError]     — 5xx, transient — driver for retry
 *  - [TransportError]  — status outside 100..599 (no response / proxy
 *                        misbehavior)
 *
 * Pure, side-effect-free, deterministic. The body decode itself stays
 * the caller's responsibility — this atom only classifies the status.
 */
sealed interface ApiCallOutcome<out T> {
    data class Success<T>(val value: T?) : ApiCallOutcome<T>

    data object Unauthorized : ApiCallOutcome<Nothing>

    data object NotFound : ApiCallOutcome<Nothing>

    data object Conflict : ApiCallOutcome<Nothing>

    data object RateLimited : ApiCallOutcome<Nothing>

    data class ClientError(val httpStatus: Int) : ApiCallOutcome<Nothing>

    data class ServerError(val httpStatus: Int) : ApiCallOutcome<Nothing>

    data object TransportError : ApiCallOutcome<Nothing>

    companion object {
        fun <T> fromHttpStatus(
            httpStatus: Int,
            body: T?,
        ): ApiCallOutcome<T> =
            when (httpStatus) {
                in 200..299 -> Success(value = body)
                401 -> Unauthorized
                404 -> NotFound
                409 -> Conflict
                429 -> RateLimited
                in 400..499 -> ClientError(httpStatus = httpStatus)
                in 500..599 -> ServerError(httpStatus = httpStatus)
                else -> TransportError
            }
    }
}
