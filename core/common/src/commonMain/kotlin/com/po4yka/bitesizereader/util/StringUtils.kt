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

/**
 * Returns a URL-like string without query or fragment data so logs can identify
 * the route without retaining user input, auth callback fields, or tokens.
 */
fun String.redactQueryAndFragment(): String {
    val queryStart = indexOf('?').takeIf { it >= 0 } ?: length
    val fragmentStart = indexOf('#').takeIf { it >= 0 } ?: length
    return take(minOf(queryStart, fragmentStart))
}
