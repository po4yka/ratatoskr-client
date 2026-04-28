package com.po4yka.ratatoskr.domain.repository

interface ProxyRepository {
    /**
     * Build the full proxied image URL for a given remote image URL.
     *
     * @param url Original image URL to proxy
     * @return Full proxy URL suitable for image loading
     */
    fun getProxiedImageUrl(url: String): String
}
