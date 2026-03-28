package com.po4yka.bitesizereader

import com.po4yka.bitesizereader.ios.IosAppBridge
import com.po4yka.bitesizereader.presentation.navigation.RootComponent

/**
 * Swift-facing convenience wrapper around [IosAppBridge].
 *
 * Default Kotlin arguments on [IosAppBridge] are not exported to Swift, so the
 * host app instantiates this wrapper instead of wiring bridge dependencies
 * manually on the Swift side.
 */
class IosAppHost {
    private val bridge = IosAppBridge()

    val rootComponent: RootComponent
        get() = bridge.rootComponent

    fun onStart() = bridge.onStart()

    fun onResume() = bridge.onResume()

    fun onPause() = bridge.onPause()

    fun onStop() = bridge.onStop()

    fun onDestroy() = bridge.onDestroy()

    suspend fun runBackgroundSync(forceFull: Boolean = false) = bridge.runBackgroundSync(forceFull)

    suspend fun refreshRecentSummaries(limit: Int = 3): Int = bridge.refreshRecentSummaries(limit)
}
