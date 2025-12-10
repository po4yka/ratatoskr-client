package com.po4yka.bitesizereader

interface Platform {
    val name: String
    fun restartApp()
}

expect fun getPlatform(): Platform
