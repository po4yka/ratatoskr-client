package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.domain.repository.ProxyRepository
import com.po4yka.ratatoskr.util.config.AppConfig
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import org.koin.core.annotation.Single

@Single(binds = [ProxyApi::class, ProxyRepository::class])
class KtorProxyApi : ProxyApi, ProxyRepository {
    override fun getProxiedImageUrl(url: String): String {
        return URLBuilder()
            .apply {
                takeFrom(AppConfig.Api.baseUrl)
                appendPathSegments("v1", "proxy", "image")
                parameters.append("url", url)
            }
            .buildString()
    }
}
