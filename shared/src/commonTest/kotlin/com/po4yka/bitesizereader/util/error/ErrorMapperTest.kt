package com.po4yka.bitesizereader.util.error

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorMapperTest {
    private val provider = DefaultErrorMessageProvider

    @Test
    fun `maps http status codes to friendly errors`() {
        val unauthorized = HttpStatusCode.Unauthorized.toAppError()
        assertTrue(unauthorized is AppError.AuthError)
        assertEquals("error.auth.unauthorized", unauthorized.messageKey)
        assertEquals("Unauthorized. Please log in again.", unauthorized.userMessage(provider))

        val notFound = HttpStatusCode.NotFound.toAppError()
        assertTrue(notFound is AppError.ServerError)
        assertEquals("error.http.404", notFound.messageKey)
        assertEquals("Resource not found.", notFound.userMessage(provider))

        val internal = HttpStatusCode.InternalServerError.toAppError()
        assertTrue(internal is AppError.ServerError)
        assertEquals("error.http.500", internal.messageKey)
        assertEquals("Internal server error.", internal.userMessage(provider))
    }

    @Test
    fun `maps network and timeout exceptions`() {
        val ioError = IOException("no connection").toAppError()
        assertTrue(ioError is AppError.NetworkError)
        assertEquals("error.network.unreachable", ioError.messageKey)
        assertEquals(
            "Network connection unavailable. Please check your connection.",
            ioError.userMessage(provider),
        )

        val timeout = HttpRequestTimeoutException().toAppError()
        assertTrue(timeout is AppError.TimeoutError)
        assertEquals("error.network.timeout", timeout.messageKey)
        assertEquals("Request timed out. Please try again.", timeout.userMessage(provider))
    }

    @Test
    fun `falls back to provided message when key not mapped`() {
        val validation =
            AppError.ValidationError(
                messageKey = "error.validation.custom",
                fallbackMessage = "Validation failed",
            )
        assertEquals("Validation failed", validation.userMessage(provider))
    }
}
