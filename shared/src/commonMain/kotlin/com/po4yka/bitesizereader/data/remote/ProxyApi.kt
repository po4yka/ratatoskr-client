package com.po4yka.bitesizereader.data.remote

/**
 * Proxy API for bypassing CORS/hotlink protection on remote images.
 *
 * The proxy endpoint returns a binary image stream, so this API
 * constructs the full URL for use with image loaders (Coil) rather
 * than fetching the bytes directly.
 */
interface ProxyApi {
    /**
     * Build the full proxied image URL for a given remote image URL.
     *
     * @param url Original image URL to proxy
     * @return Full proxy URL suitable for Coil image loading
     */
    fun getProxiedImageUrl(url: String): String
}
