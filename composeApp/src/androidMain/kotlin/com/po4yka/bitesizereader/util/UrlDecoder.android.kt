package com.po4yka.bitesizereader.util

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

actual object UrlDecoder {
    actual fun decode(part: String): String {
        return try {
            URLDecoder.decode(part, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            part
        }
    }
}
