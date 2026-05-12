package com.po4yka.ratatoskr.api.generated.bootstrap

import com.kroegerama.openapi.kmp.gen.companion.AuthItem
import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.http.Url

/**
 * Bootstraps the generated [Api] singleton. Call this once during app
 * startup, before any generated `*Api` object methods are invoked.
 *
 * The generated client is a singleton (`object Api : ApiHolder()`) that
 * owns its own [io.ktor.client.HttpClient]. It is intentionally separate
 * from `core/data`'s `ApiClient`, which still serves the hand-written
 * `feature/<name>/data/remote/<Name>Api.kt` call sites during the incremental
 * migration to the generated client.
 *
 * @param baseUrl base URL for the mobile API (e.g. `AppConfig.Api.baseUrl`).
 * @param engine platform Ktor engine — pass the same one `core/data` uses
 *   (OkHttp on Android, Darwin on iOS, OkHttp on desktop).
 * @param bearerTokenProvider invoked on every authenticated request to
 *   read the latest token. Implement against `SecureStorage` to track
 *   token rotation. Return `null` when there is no session.
 * @param withLogging enables Ktor's request/response logger. Match
 *   `AppConfig.Api.loggingEnabled`.
 * @param withCompression enables gzip request/response encoding.
 * @param extraConfig optional decorator for the underlying [HttpClientConfig].
 *
 * NOTE: refresh-on-401 is NOT installed here. The companion library's
 * `AuthPlugin` only PROVIDES the bearer token; it does not refresh.
 * Consumers of the generated APIs handle 401 explicitly via the
 * `Either<CallException, HttpCallResponse<T>>` return type. Refresh
 * semantics live in the consumer migration follow-up.
 */
public fun bootstrapGeneratedApi(
    baseUrl: String,
    engine: HttpClientEngine,
    bearerTokenProvider: suspend () -> String?,
    withLogging: Boolean = false,
    withCompression: Boolean = true,
    extraConfig: HttpClientConfig<*>.() -> Unit = {},
) {
    Api.baseUrl = Url(baseUrl)
    Api.updateClient(
        withLogging = withLogging,
        withCompression = withCompression,
        createHttpClient = { decorator ->
            io.ktor.client.HttpClient(engine) {
                decorator()
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
