package com.po4yka.ratatoskr.api.generated.bootstrap

import com.kroegerama.openapi.kmp.gen.companion.AuthItem
import com.kroegerama.openapi.kmp.gen.companion.JWT
import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestRetryConfig
import io.ktor.http.Url

/**
 * Bootstraps the generated [Api] singleton. Call once at app startup,
 * before any generated `*Api` object method is invoked.
 *
 * The generated client is a singleton (`object Api : ApiHolder()`) that
 * owns its own [io.ktor.client.HttpClient]. It is intentionally separate
 * from `core/data`'s `ApiClient`, which still serves the hand-written
 * `feature/<name>/data/remote/<Name>Api.kt` call sites during the
 * incremental migration to the generated client.
 *
 * @param baseUrl base URL for the mobile API (e.g. `AppConfig.Api.baseUrl`).
 * @param engine platform Ktor engine — pass the same one `core/data` uses
 *   (OkHttp on Android, Darwin on iOS, OkHttp on desktop).
 * @param bearerTokenProvider invoked on every authenticated request to
 *   read the latest token. Implement proactive refresh inside this
 *   closure: parse the cached access token with [isAccessTokenExpired],
 *   call the refresh endpoint, persist the new tokens, and return the
 *   fresh access token. Return `null` when there is no session.
 * @param withLogging enables Ktor's request/response logger. Match
 *   `AppConfig.Api.loggingEnabled`.
 * @param withCompression enables gzip request/response encoding.
 * @param retryConfig customizes the [HttpRequestRetry] plugin that
 *   replaces feature-side `retryWithBackoff` helpers. Default policy
 *   retries 3x on 5xx and IO exceptions with exponential backoff.
 * @param extraConfig optional decorator for the underlying [HttpClientConfig].
 */
public fun bootstrapGeneratedApi(
    baseUrl: String,
    engine: HttpClientEngine,
    bearerTokenProvider: suspend () -> String?,
    withLogging: Boolean = false,
    withCompression: Boolean = true,
    retryConfig: HttpRequestRetryConfig.() -> Unit = defaultRetryConfig,
    extraConfig: HttpClientConfig<*>.() -> Unit = {},
) {
    Api.baseUrl = Url(baseUrl)
    Api.updateClient(
        withLogging = withLogging,
        withCompression = withCompression,
        createHttpClient = { decorator ->
            io.ktor.client.HttpClient(engine) {
                decorator()
                install(HttpRequestRetry, retryConfig)
                extraConfig()
            }
        },
    )
    Api.setAuthProvider(
        Auth.HTTPBearer(
            getBearer = {
                bearerTokenProvider()?.let { AuthItem.Bearer(it) }
            },
        ),
    )
}

/**
 * Clears the bearer auth provider. Call on logout. Subsequent
 * authenticated requests will be issued without an `Authorization`
 * header until [bootstrapGeneratedApi] is invoked again.
 */
public fun clearGeneratedApiAuth() {
    Api.clearAuthProvider(Auth.HTTPBearer(getBearer = { null }))
}

/**
 * Parses [accessToken] as a JWT and returns `true` if it has expired
 * (or is about to expire within [skewSeconds]). Returns `true` for
 * unparseable tokens so callers refresh defensively.
 *
 * Use inside a `bearerTokenProvider` to drive proactive refresh.
 */
public fun isAccessTokenExpired(accessToken: String, skewSeconds: Long = 30L): Boolean {
    val jwt = JWT.parseOrNull(accessToken) ?: return true
    return jwt.isExpired(skewSeconds)
}

private val defaultRetryConfig: HttpRequestRetryConfig.() -> Unit = {
    retryOnServerErrors(maxRetries = 3)
    retryOnException(maxRetries = 3, retryOnTimeout = true)
    exponentialDelay()
}
