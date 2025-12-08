package com.po4yka.bitesizereader.util

import platform.Foundation.NSString
import platform.Foundation.stringByRemovingPercentEncoding

actual object UrlDecoder {
    actual fun decode(part: String): String {
        return (part as NSString).stringByRemovingPercentEncoding ?: part
    }
}
