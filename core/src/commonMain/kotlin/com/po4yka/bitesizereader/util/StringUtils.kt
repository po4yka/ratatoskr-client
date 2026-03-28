package com.po4yka.bitesizereader.util

/**
 * Extracts the domain (host) portion from a URL string.
 *
 * @return the domain or null if the URL is blank after stripping the protocol and path.
 */
fun extractDomain(url: String): String? {
    val noProtocol = url.substringAfter("://", url)
    return noProtocol.substringBefore("/").ifBlank { null }
}
