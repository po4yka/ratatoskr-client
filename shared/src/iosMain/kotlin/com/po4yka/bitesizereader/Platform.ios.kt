package com.po4yka.bitesizereader

import platform.UIKit.UIDevice
import platform.posix.exit

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override fun restartApp() {
        exit(0)
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
