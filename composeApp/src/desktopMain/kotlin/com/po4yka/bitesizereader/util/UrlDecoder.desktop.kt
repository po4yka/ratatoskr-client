package com.po4yka.bitesizereader.util

import java.net.URLDecoder

/**
 * Desktop implementation of UrlDecoder using Java's URLDecoder
 */
actual object UrlDecoder {
    actual fun decode(part: String): String {
        return URLDecoder.decode(part, Charsets.UTF_8.name())
    }
}
