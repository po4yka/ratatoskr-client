package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.ProxyRepository
import org.koin.core.annotation.Factory

@Factory
class GetProxiedImageUrlUseCase(private val proxyRepository: ProxyRepository) {
    operator fun invoke(url: String): String {
        return proxyRepository.getProxiedImageUrl(url)
    }
}
