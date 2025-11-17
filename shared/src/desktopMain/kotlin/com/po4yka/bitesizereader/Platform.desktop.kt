package com.po4yka.bitesizereader

class DesktopPlatform : Platform {
    override val name: String = "Desktop JVM (${System.getProperty("os.name")})"
}

actual fun getPlatform(): Platform = DesktopPlatform()
