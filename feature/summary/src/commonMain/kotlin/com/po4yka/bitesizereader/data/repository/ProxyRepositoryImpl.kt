package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.ProxyApi
import com.po4yka.bitesizereader.domain.repository.ProxyRepository
import org.koin.core.annotation.Single

@Single(binds = [ProxyRepository::class])
class ProxyRepositoryImpl(
    private val api: ProxyApi,
) : ProxyRepository {
    override fun getProxiedImageUrl(url: String): String {
        return api.getProxiedImageUrl(url)
    }
}
