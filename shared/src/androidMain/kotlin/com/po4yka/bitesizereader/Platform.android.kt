package com.po4yka.bitesizereader

import android.content.Context
import android.content.Intent
import android.os.Build
import kotlin.system.exitProcess

internal lateinit var platformContext: Context

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun restartApp() {
        val launchIntent = platformContext.packageManager.getLaunchIntentForPackage(platformContext.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            platformContext.startActivity(launchIntent)
        }
        exitProcess(0)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
