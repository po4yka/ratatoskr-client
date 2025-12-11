package com.po4yka.bitesizereader

class DesktopPlatform : Platform {
    override val name: String = "Desktop JVM (${System.getProperty("os.name")})"

    override fun restartApp() {
        // Desktop doesn't support automatic app restart
        // This is a development-only target
        println("App restart requested - please restart the application manually")
    }
}

actual fun getPlatform(): Platform = DesktopPlatform()
