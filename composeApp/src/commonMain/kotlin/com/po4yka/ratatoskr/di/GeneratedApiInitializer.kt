package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.api.generated.api.AuthenticationApi
import com.po4yka.ratatoskr.api.generated.bootstrap.bootstrapGeneratedApi
import com.po4yka.ratatoskr.api.generated.bootstrap.isAccessTokenExpired
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.RefreshTokenRequest
import com.po4yka.ratatoskr.data.local.SecureStorage
import com.po4yka.ratatoskr.data.remote.trace.TraceHeadersPlugin
import com.po4yka.ratatoskr.util.config.AppConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin

private val moduleLogger = KotlinLogging.logger("GeneratedApiBootstrap")
private val refreshMutex = Mutex()

/**
 * Initialize the generated `Api` singleton. Call once, immediately after
 * `initKoin(...)` returns, before any generated API method is invoked.
 *
 * Wires:
 * - baseUrl from [AppConfig.Api.baseUrl]
 * - the same [HttpClientEngine] used by `core/data`'s legacy `ApiClient`
 * - bearer-token provider backed by [SecureStorage] with proactive
 *   JWT-expiry refresh via the generated [AuthenticationApi.refreshAccessTokenV1AuthRefreshPost]
 * - Ktor `HttpRequestRetry` (default policy from `bootstrapGeneratedApi`)
 *
 * The hand-written `core/data` `ApiClient` continues to coexist while any
 * code paths still call it; both clients share the same auth source of
 * truth ([SecureStorage]).
 */
fun Koin.bootstrapGeneratedApiFromKoin() {
    val engine = getKoin().get<HttpClientEngine>()
    val secureStorage = getKoin().get<SecureStorage>()
    val provider: suspend () -> String? = { resolveBearerToken(secureStorage) }
    bootstrapGeneratedApi(
        baseUrl = AppConfig.Api.baseUrl.trimEnd('/'),
        engine = engine,
        bearerTokenProvider = provider,
        withLogging = AppConfig.Api.loggingEnabled,
        extraConfig = {
            install(TraceHeadersPlugin)
        },
    )
    moduleLogger.info { "Generated Api singleton bootstrapped" }
}

@Suppress("unused") private fun Koin.getKoin(): org.koin.core.Koin = this

private suspend fun resolveBearerToken(secureStorage: SecureStorage): String? {
    val cached = secureStorage.getAccessToken() ?: return null
    if (!isAccessTokenExpired(cached)) return cached
    return refreshMutex.withLock {
        val recheck = secureStorage.getAccessToken() ?: return@withLock null
        if (!isAccessTokenExpired(recheck)) return@withLock recheck
        val refresh = secureStorage.getRefreshToken() ?: return@withLock null
        runCatching {
            AuthenticationApi
                .refreshAccessTokenV1AuthRefreshPost(RefreshTokenRequest(refreshToken = refresh))
                .unwrap()
        }.fold(
            onSuccess = { envelope ->
                val tokens = envelope.data ?: return@fold recheck
                secureStorage.saveAccessToken(tokens.accessToken)
                tokens.refreshToken?.takeIf { it.isNotEmpty() }
                    ?.let { secureStorage.saveRefreshToken(it) }
                tokens.accessToken
            },
            onFailure = { e ->
                moduleLogger.warn(e) { "Proactive token refresh failed; returning cached token" }
                recheck
            },
        )
    }
}
