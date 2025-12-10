package com.po4yka.bitesizereader

import android.content.Context
import android.content.Intent
import android.os.Build
import kotlin.system.exitProcess

class AndroidPlatform(private val context: Context?) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun restartApp() {
        if (context != null) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(launchIntent)
            }
            exitProcess(0)
        } else {
            // Fallback or log if context is missing (should not happen when injected)
            exitProcess(0)
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform(null)
