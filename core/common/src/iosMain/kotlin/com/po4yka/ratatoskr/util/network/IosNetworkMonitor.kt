@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("WildcardImport") // Platform APIs are grouped together

package com.po4yka.ratatoskr.util.network

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.Network.nw_path_t
import platform.darwin.dispatch_get_main_queue

/**
 * iOS implementation of NetworkMonitor using Network framework
 *
 * Important: Call cancel() when no longer needed to release the native nw_path_monitor resource.
 * This is handled automatically by Koin's onClose callback in the DI module.
 */
class IosNetworkMonitor : NetworkMonitor {
    private val _networkStatus = MutableStateFlow(NetworkStatus.UNKNOWN)
    override val networkStatus: Flow<NetworkStatus> = _networkStatus

    private val monitor = nw_path_monitor_create()

    init {
        setupMonitor()
    }

    private fun setupMonitor() {
        nw_path_monitor_set_update_handler(monitor) { path: nw_path_t? ->
            val pathStatus = path?.let { nw_path_get_status(it) }
            val status =
                if (pathStatus == nw_path_status_satisfied) {
                    NetworkStatus.CONNECTED
                } else {
                    NetworkStatus.DISCONNECTED
                }
            _networkStatus.value = status
        }

        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
    }

    override suspend fun isConnected(): Boolean {
        return _networkStatus.value == NetworkStatus.CONNECTED
    }

    @Suppress("unused") // Called from Koin DI cleanup
    fun cancel() {
        nw_path_monitor_cancel(monitor)
    }
}
