package com.po4yka.bitesizereader.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class KtorSyncApiTest {
    @Test
    fun `full sync sends cursor query parameter when provided`() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val api = KtorSyncApi(testClient(requests))

            api.fullSync(sessionId = "session-1", limit = 50, cursor = 123)

            val query = requests.single().url.parameters
            assertEquals("session-1", query["session_id"])
            assertEquals("50", query["limit"])
            assertEquals("123", query["cursor"])
        }

    @Test
    fun `full sync omits cursor query parameter when not provided`() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val api = KtorSyncApi(testClient(requests))

            api.fullSync(sessionId = "session-1", limit = 50)

            val query = requests.single().url.parameters
            assertEquals("session-1", query["session_id"])
            assertEquals("50", query["limit"])
            assertNull(query["cursor"])
        }

    private fun testClient(requests: MutableList<HttpRequestData>): HttpClient =
        HttpClient(
            MockEngine { request ->
                requests += request
                respond(
                    content = """{"success":true,"data":{"items":[],"hasMore":false}}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            },
        ) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
}
