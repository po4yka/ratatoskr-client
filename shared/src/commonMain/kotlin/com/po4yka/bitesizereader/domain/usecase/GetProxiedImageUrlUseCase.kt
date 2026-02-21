package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.ProxyApi
import org.koin.core.annotation.Factory

@Factory
class GetProxiedImageUrlUseCase(private val proxyApi: ProxyApi) {
    operator fun invoke(url: String): String {
        return proxyApi.getProxiedImageUrl(url)
    }
}
