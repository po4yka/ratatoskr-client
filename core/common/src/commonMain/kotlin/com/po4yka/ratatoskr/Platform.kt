package com.po4yka.ratatoskr

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
