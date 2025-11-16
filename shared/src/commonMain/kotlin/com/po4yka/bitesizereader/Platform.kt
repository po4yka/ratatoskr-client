package com.po4yka.bitesizereader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform