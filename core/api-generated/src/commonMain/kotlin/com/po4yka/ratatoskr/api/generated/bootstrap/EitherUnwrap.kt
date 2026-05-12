package com.po4yka.ratatoskr.api.generated.bootstrap

import arrow.core.Either
import com.kroegerama.openapi.kmp.gen.companion.CallException
import com.kroegerama.openapi.kmp.gen.companion.HttpCallResponse

/**
 * Unwraps a generated API call result into its data payload, throwing on
 * any failure mode. Intended for consumer migration where the existing
 * code path is `throw on error, return T on success` and the caller does
 * not want to thread `Either` through every layer.
 *
 * Throws:
 * - the underlying [CallException] (a [RuntimeException] subclass) if the
 *   left arm was taken (network failure, deserialization error, etc.);
 * - [HttpFailureException] if the HTTP response was non-2xx, carrying the
 *   status code and body for the caller to map to a domain error.
 *
 * For new code, prefer using [Either.fold] or `flatMap` chains directly
 * rather than this throw-on-failure shim.
 */
public fun <T> Either<CallException, HttpCallResponse<T>>.unwrap(): T =
    fold(
        ifLeft = { throw it },
        ifRight = { response ->
            if (response.isSuccessful) {
                response.data
            } else {
                throw HttpFailureException(
                    code = response.code,
                    message = response.message,
                    body = response.data,
                )
            }
        },
    )

/**
 * Signals that a generated API call completed at the HTTP layer but with
 * a non-2xx status. Carries enough context for the caller to translate
 * into a domain error.
 */
public class HttpFailureException(
    public val code: Int,
    message: String,
    public val body: Any?,
) : RuntimeException("HTTP $code: $message")
